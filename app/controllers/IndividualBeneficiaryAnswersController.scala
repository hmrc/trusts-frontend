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
import navigation.Navigator
import pages.{IndividualBeneficiaryAnswersPage, IndividualBeneficiaryNamePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import viewmodels.AnswerSection
import views.html.IndividualBenficiaryAnswersView
import utils.countryOptions.CountryOptions

import scala.concurrent.ExecutionContext

class IndividualBeneficiaryAnswersController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       identify: IdentifierAction,
                                       navigator: Navigator,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       requiredAnswer: RequiredAnswerActionProvider,
                                       validateIndex : IndexActionFilterProvider,
                                       view: IndividualBenficiaryAnswersView,
                                       countryOptions : CountryOptions
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {


  private def actions(index : Int) =
    identify andThen
      getData andThen
      requireData andThen
      requiredAnswer(RequiredAnswer(IndividualBeneficiaryNamePage(index), routes.IndividualBeneficiaryNameController.onPageLoad(NormalMode, 0)))



  def onPageLoad(index : Int): Action[AnyContent] = actions(index) {
    implicit request =>

      val answers = new CheckYourAnswersHelper(countryOptions)(request.userAnswers)

      val sections = Seq(
        AnswerSection(
          None,
          Seq(
            answers.individualBeneficiaryName(index),
            answers.individualBeneficiaryDateOfBirthYesNo(index),
            answers.individualBeneficiaryDateOfBirth(index),
            answers.individualBeneficiaryIncomeYesNo(index),
            answers.individualBeneficiaryNationalInsuranceYesNo(index),
            answers.individualBeneficiaryNationalInsuranceNumber(index),
            answers.individualBeneficiaryAddressYesNo(index),
            answers.individualBeneficiaryAddressUK(index),
            answers.individualBeneficiaryVulnerableYesNo(index)
          ).flatten
        )
      )

      Ok(view(index, sections))
  }

  def onSubmit(index : Int) = actions(index) {
    implicit request =>
      Redirect(navigator.nextPage(IndividualBeneficiaryAnswersPage, NormalMode)(request.userAnswers))
  }
}
