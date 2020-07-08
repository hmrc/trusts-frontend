/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package repositories

import connector.SubmissionDraftConnector
import javax.inject.Inject
import models.RegistrationSubmission
import models.RegistrationSubmission.{AllAnswerSections, AllStatus}
import models.core.UserAnswers
import models.registration.pages.RegistrationStatus.Complete
import models.registration.pages.Status
import pages.register.agents.AgentInternalReferencePage
import play.api.http
import play.api.libs.json._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.http.HeaderCarrier
import utils.DateFormatter
import viewmodels.{AnswerRow, AnswerSection, DraftRegistration, RegistrationAnswerSections}

import scala.concurrent.{ExecutionContext, Future}

class DefaultRegistrationsRepository @Inject()(dateFormatter: DateFormatter,
                                          submissionDraftConnector: SubmissionDraftConnector
                                        )(implicit ec: ExecutionContext) extends RegistrationsRepository {

  override def get(draftId: String)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]] = {
    submissionDraftConnector.getDraftMain(draftId).map {
      response => Some(response.data.as[UserAnswers])
    }
  }

  override def listDrafts()(implicit hc: HeaderCarrier) : Future[List[DraftRegistration]] = {
    submissionDraftConnector.getCurrentDraftIds().map {
      draftIds =>
        draftIds.flatMap {
          x => x.reference.map {
            reference => DraftRegistration(x.draftId, reference, dateFormatter.savedUntil(x.createdAt))
          }
        }
    }
  }

  override def set(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] = {

    val reference =
        userAnswers.get(AgentInternalReferencePage).map {
          reference => reference
        }.orElse(None)

    submissionDraftConnector.setDraftMain(
      userAnswers.draftId,
      Json.toJson(userAnswers),
      userAnswers.progress != Complete,
      reference
    ).map {
      response => response.status == http.Status.OK
    }
  }

  private def decodePath(encodedPath: String): JsPath =
    encodedPath.split('/').foldLeft[JsPath](
      JsPath
    )(
      (cur: JsPath, component: String) => cur \ component
    )

  private def addSection(key: String, section: JsValue, data: JsValue): JsResult[JsValue] = {
    val path = decodePath(key).json
    val transform = data.transform(path.pick) match {
      case JsSuccess(_,_) => path.prune andThen __.json.update(path.put(section))
      case _ => __.json.update(path.put(section))
    }

    data.transform(transform)
  }

  override def addDraftRegistrationSections(draftId: String, registrationJson: JsValue)(implicit hc: HeaderCarrier) : Future[JsValue] = {
    submissionDraftConnector.getRegistrationPieces(draftId).map {
      pieces =>
        val added: JsResult[JsValue] = pieces.keys.foldLeft[JsResult[JsValue]](
          JsSuccess(registrationJson)
        )(
          (cur, key) => cur.flatMap(addSection(key, pieces(key), _))
        )

        added match {
          case JsSuccess(value, _) => value
          case _ => registrationJson
        }
    }
  }

  override def getAllStatus(draftId: String)(implicit hc: HeaderCarrier) : Future[AllStatus] = {
    submissionDraftConnector.getStatus(draftId)
  }

  override def getAnswerSections(draftId: String)(implicit hc:HeaderCarrier) : Future[RegistrationAnswerSections] = {
    submissionDraftConnector.getAnswerSections(draftId).map {
      sections => RegistrationAnswerSections(
        beneficiaries = convert(sections.beneficiaries)
      )
    }
  }

  private def convert(row: RegistrationSubmission.AnswerRow): AnswerRow =
    AnswerRow(row.label, HtmlFormat.raw(row.answer), None, row.labelArg, canEdit = false)

  private def convert(section: RegistrationSubmission.AnswerSection): AnswerSection =
    AnswerSection(section.headingKey, section.rows.map(convert), section.sectionKey)

  private def convert(section: Option[List[RegistrationSubmission.AnswerSection]]): Option[List[AnswerSection]] =
    section map { _.map(convert) }

}

trait RegistrationsRepository {
  def get(draftId: String)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]]

  def set(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean]

  def listDrafts()(implicit hc: HeaderCarrier) : Future[List[DraftRegistration]]

  def addDraftRegistrationSections(draftId: String, registrationJson: JsValue)(implicit hc: HeaderCarrier) : Future[JsValue]

  def getAllStatus(draftId: String)(implicit hc: HeaderCarrier) : Future[AllStatus]

  def getAnswerSections(draftId: String)(implicit hc:HeaderCarrier) : Future[RegistrationAnswerSections]
}
