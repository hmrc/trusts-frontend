/*
 * Copyright 2019 HM Revenue & Customs
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

package controllers

import controllers.actions._
import controllers.filters.IndexActionFilterProvider
import javax.inject.Inject
import models.NormalMode
import models.Status.Completed
import navigation.Navigator
import pages.TrustDetailsAnswerPage
import pages.entitystatus.TrustDetailsStatus
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.TrustDetailsAnswerPageView

import scala.concurrent.{ExecutionContext, Future}


class TrustDetailsAnswerPageController @Inject()(
                                              sessionRepository: SessionRepository,
                                              override val messagesApi: MessagesApi,
                                              identify: IdentifierAction,
                                              navigator: Navigator,
                                              getData: DraftIdRetrievalActionProvider,
                                              requireData: DataRequiredAction,
                                              validateIndex : IndexActionFilterProvider,
                                              val controllerComponents: MessagesControllerComponents,
                                              view: TrustDetailsAnswerPageView,
                                              countryOptions : CountryOptions
                                            )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String) =
    identify andThen getData(draftId) andThen requireData

  def onPageLoad(draftId: String) = actions(draftId) {
    implicit request =>

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(request.userAnswers, draftId)

      val sections = Seq(
        AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.trustName,
            checkYourAnswersHelper.whenTrustSetup,
            checkYourAnswersHelper.governedInsideTheUK,
            checkYourAnswersHelper.countryGoverningTrust,
            checkYourAnswersHelper.administrationInsideUK,
            checkYourAnswersHelper.countryAdministeringTrust,
            checkYourAnswersHelper.trusteesBasedInUK,
            checkYourAnswersHelper.settlorsBasedInTheUK,
            checkYourAnswersHelper.establishedUnderScotsLaw,
            checkYourAnswersHelper.trustResidentOffshore,
            checkYourAnswersHelper.trustPreviouslyResident,
            checkYourAnswersHelper.registeringTrustFor5A,
            checkYourAnswersHelper.nonresidentType,
            checkYourAnswersHelper.inheritanceTaxAct,
            checkYourAnswersHelper.agentOtherThanBarrister
          ).flatten
        )
      )

      Ok(view(draftId, sections))
  }

  def onSubmit(draftId: String) = actions(draftId).async {
    implicit request =>

      for {
        updatedAnswers <- Future.fromTry(request.userAnswers.set(TrustDetailsStatus, Completed))
        _              <- sessionRepository.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(TrustDetailsAnswerPage, NormalMode, draftId)(request.userAnswers))
  }
}