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
import javax.inject.Inject
import models.NormalMode
import models.Status.Completed
import navigation.Navigator
import pages.{DeceasedSettlorAnswerPage, DeceasedSettlorComplete, SettlorsNamePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.DeceasedSettlorAnswerView

import scala.concurrent.{ExecutionContext, Future}

class DeceasedSettlorAnswerController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       navigator: Navigator,
                                       requireData: DataRequiredAction,
                                       requiredAnswer: RequiredAnswerActionProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: DeceasedSettlorAnswerView,
                                       countryOptions : CountryOptions
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions() =
    identify andThen
      getData andThen
      requireData andThen
      requiredAnswer(RequiredAnswer(SettlorsNamePage, routes.SettlorsNameController.onPageLoad(NormalMode)))


  def onPageLoad: Action[AnyContent] = actions() {
    implicit request =>

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(request.userAnswers)

      val sections = Seq(
        AnswerSection(
          None,
          Seq(checkYourAnswersHelper.setupAfterSettlorDied,
            checkYourAnswersHelper.settlorsName,
            checkYourAnswersHelper.settlorDateOfDeathYesNo,
            checkYourAnswersHelper.settlorDateOfDeath,
            checkYourAnswersHelper.settlorDateOfBirthYesNo,
            checkYourAnswersHelper.settlorsDateOfBirth,
            checkYourAnswersHelper.settlorsNINoYesNo,
            checkYourAnswersHelper.settlorNationalInsuranceNumber,
            checkYourAnswersHelper.settlorsLastKnownAddressYesNo,
            checkYourAnswersHelper.wasSettlorsAddressUKYesNo,
            checkYourAnswersHelper.settlorsUKAddress,
            checkYourAnswersHelper.settlorsInternationalAddress
          ).flatten
        )
      )

      Ok(view(sections))
  }

  def onSubmit() = actions().async {
    implicit request =>

      for {
        updatedAnswers <- Future.fromTry(request.userAnswers.set(DeceasedSettlorComplete, Completed))
        _              <- sessionRepository.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(DeceasedSettlorAnswerPage, NormalMode)(request.userAnswers))
  }
}
