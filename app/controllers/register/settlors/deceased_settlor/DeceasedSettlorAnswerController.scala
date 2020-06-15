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

package controllers.register.settlors.deceased_settlor

import controllers.actions._
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import javax.inject.Inject
import models.NormalMode
import models.registration.pages.Status.Completed
import navigation.Navigator
import pages.entitystatus.DeceasedSettlorStatus
import pages.register.settlors.deceased_settlor.{DeceasedSettlorAnswerPage, SettlorsNamePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.register.settlors.deceased_settlor.DeceasedSettlorAnswerView

import scala.concurrent.{ExecutionContext, Future}

class DeceasedSettlorAnswerController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 registrationsRepository: RegistrationsRepository,
                                                 identify: RegistrationIdentifierAction,
                                                 getData: DraftIdRetrievalActionProvider,
                                                 navigator: Navigator,
                                                 requireData: RegistrationDataRequiredAction,
                                                 requiredAnswer: RequiredAnswerActionProvider,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 view: DeceasedSettlorAnswerView,
                                                 countryOptions : CountryOptions
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String) =
    identify andThen
      getData(draftId) andThen
      requireData andThen
      requiredAnswer(RequiredAnswer(SettlorsNamePage, routes.SettlorsNameController.onPageLoad(NormalMode, draftId)))


  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(request.userAnswers, draftId, canEdit = true)

      val sections = Seq(
        AnswerSection(
          None,
          Seq(checkYourAnswersHelper.setUpAfterSettlorDied,
            checkYourAnswersHelper.setUpInAddition,
            checkYourAnswersHelper.deceasedSettlorsName,
            checkYourAnswersHelper.deceasedSettlorDateOfDeathYesNo,
            checkYourAnswersHelper.deceasedSettlorDateOfDeath,
            checkYourAnswersHelper.deceasedSettlorDateOfBirthYesNo,
            checkYourAnswersHelper.deceasedSettlorsDateOfBirth,
            checkYourAnswersHelper.deceasedSettlorsNINoYesNo,
            checkYourAnswersHelper.deceasedSettlorNationalInsuranceNumber,
            checkYourAnswersHelper.deceasedSettlorsLastKnownAddressYesNo,
            checkYourAnswersHelper.wasSettlorsAddressUKYesNo,
            checkYourAnswersHelper.deceasedSettlorsUKAddress,
            checkYourAnswersHelper.deceasedSettlorsInternationalAddress
          ).flatten
        )
      )

      Ok(view(draftId, sections))
  }

  def onSubmit(draftId: String) = actions(draftId).async {
    implicit request =>

      for {
        updatedAnswers <- Future.fromTry(request.userAnswers.set(DeceasedSettlorStatus, Completed))
        _              <- registrationsRepository.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(DeceasedSettlorAnswerPage, NormalMode, draftId)(request.userAnswers))
  }
}
