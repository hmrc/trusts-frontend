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

package controllers.register.trust_details

import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import controllers.filters.IndexActionFilterProvider
import javax.inject.Inject
import models.NormalMode
import models.registration.pages.Status.Completed
import navigation.Navigator
import pages.entitystatus.TrustDetailsStatus
import pages.register.TrustDetailsAnswerPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.register.trust_details.TrustDetailsAnswerPageView

import scala.concurrent.{ExecutionContext, Future}


class TrustDetailsAnswerPageController @Inject()(
                                                  registrationsRepository: RegistrationsRepository,
                                                  override val messagesApi: MessagesApi,
                                                  identify: RegistrationIdentifierAction,
                                                  navigator: Navigator,
                                                  getData: DraftIdRetrievalActionProvider,
                                                  requireData: RegistrationDataRequiredAction,
                                                  validateIndex : IndexActionFilterProvider,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  view: TrustDetailsAnswerPageView,
                                                  countryOptions : CountryOptions
                                            )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String) =
    identify andThen getData(draftId) andThen requireData

  def onPageLoad(draftId: String) = actions(draftId) {
    implicit request =>

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(request.userAnswers, draftId, canEdit = true)

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
        _              <- registrationsRepository.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(TrustDetailsAnswerPage, NormalMode, draftId)(request.userAnswers))
  }
}