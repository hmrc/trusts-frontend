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

package services

import java.util.UUID

import akka.stream.Materializer
import connector.SubmissionDraftConnector
import javax.inject.Inject
import models.core.{ReadOnlyUserAnswers, UserAnswers}
import models.registration.pages.Status.InProgress
import models.requests.{IdentifierRequest, OptionalRegistrationDataRequest}
import pages.register.beneficiaries.individual.RoleInCompanyPage
import repositories.RegistrationsRepository
import sections.beneficiaries.IndividualBeneficiaries
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import viewmodels.RegistrationAnswerSections

import scala.concurrent.{ExecutionContext, Future}

class DraftRegistrationService @Inject()(
                                          registrationsRepository: RegistrationsRepository,
                                          submissionDraftConnector: SubmissionDraftConnector,
                                          auditConnector: AuditConnector
                                        )(implicit ec: ExecutionContext, m: Materializer) {

  private def build[A](request: OptionalRegistrationDataRequest[A])(implicit hc: HeaderCarrier): Future[String] = {
    val draftId = UUID.randomUUID().toString
    val userAnswers = UserAnswers(draftId = draftId, internalAuthId = request.internalId)

    registrationsRepository.set(userAnswers).map {
      _ =>
        draftId
    }
  }

  def create[A](request: IdentifierRequest[A])(implicit hc: HeaderCarrier): Future[String] = {
    val transformed = OptionalRegistrationDataRequest(request.request, request.identifier, None, request.affinityGroup, request.enrolments, request.agentARN)
    build(transformed)
  }

  def create[A](request: OptionalRegistrationDataRequest[A])(implicit hc: HeaderCarrier): Future[String] =
    build(request)

  def getAnswerSections(draftId: String)(implicit hc: HeaderCarrier): Future[RegistrationAnswerSections] =
    registrationsRepository.getAnswerSections(draftId)

  def setBeneficiaryStatus(draftId: String)(implicit hc: HeaderCarrier): Future[Boolean] =
    submissionDraftConnector.getDraftBeneficiaries(draftId: String) flatMap { response =>
      val answers = response.data.asOpt[ReadOnlyUserAnswers]

      val requiredPagesAnswered: Boolean =
        answers
          .forall { beneficiaries =>
            beneficiaries.get(IndividualBeneficiaries)
              .forall {
                _.zipWithIndex.exists { x =>
                  beneficiaries.get(RoleInCompanyPage(x._2)).isDefined
                }
              }
          }

      if (!requiredPagesAnswered) {
        registrationsRepository.getAllStatus(draftId) flatMap {
          allStatus =>
            registrationsRepository.setAllStatus(draftId, allStatus.copy(beneficiaries = Some(InProgress)))
        }
      } else {
        Future.successful(true)
      }

    }

}

