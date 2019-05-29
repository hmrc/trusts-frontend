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
import pages.status.TrusteeStatus
import pages.{IsThisLeadTrusteePage, TrusteesAnswerPage, TrusteesNamePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerSection, Trustees}
import views.html.TrusteesAnswerPageView

import scala.concurrent.{ExecutionContext, Future}

class TrusteesAnswerPageController @Inject()(
                                              override val messagesApi: MessagesApi,
                                              sessionRepository: SessionRepository,
                                              identify: IdentifierAction,
                                              navigator: Navigator,
                                              getData: DataRetrievalAction,
                                              requireData: DataRequiredAction,
                                              requiredAnswer: RequiredAnswerActionProvider,
                                              validateIndex : IndexActionFilterProvider,
                                              val controllerComponents: MessagesControllerComponents,
                                              view: TrusteesAnswerPageView,
                                              countryOptions : CountryOptions
                                            )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(index : Int) =
    identify andThen getData andThen
      requireData andThen
      validateIndex(index, Trustees) andThen
      requiredAnswer(RequiredAnswer(TrusteesNamePage(index),routes.TrusteesNameController.onPageLoad(NormalMode, index))) andThen
      requiredAnswer(RequiredAnswer(IsThisLeadTrusteePage(index), routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, index)))

  def onPageLoad(index : Int) = actions(index) {
    implicit request =>

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(request.userAnswers)

      val isLead = request.userAnswers.get(IsThisLeadTrusteePage(index)).get

      val trusteeIndividualOrBusinessMessagePrefix = if (isLead) "leadTrusteeIndividualOrBusiness" else "trusteeIndividualOrBusiness"
      val trusteeFullNameMessagePrefix = if (isLead) "leadTrusteesName" else "trusteesName"

      val sections = Seq(
        AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.isThisLeadTrustee(index),
            checkYourAnswersHelper.trusteeIndividualOrBusiness(index, trusteeIndividualOrBusinessMessagePrefix),
            checkYourAnswersHelper.trusteeFullName(index, trusteeFullNameMessagePrefix),
            checkYourAnswersHelper.trusteesDateOfBirth(index),
            checkYourAnswersHelper.trusteeAUKCitizen(index),
            checkYourAnswersHelper.trusteesNino(index),
            checkYourAnswersHelper.trusteeLiveInTheUK(index),
            checkYourAnswersHelper.trusteesUkAddress(index),
            checkYourAnswersHelper.telephoneNumber(index)
          ).flatten
        )
    )

      Ok(view(index, sections))
  }

  def onSubmit(index : Int) = actions(index).async {
    implicit request =>

    val answers = request.userAnswers.set(TrusteeStatus(index), Completed)

    for {
      updatedAnswers <- Future.fromTry(answers)
      _              <- sessionRepository.set(updatedAnswers)
    } yield Redirect(navigator.nextPage(TrusteesAnswerPage, NormalMode)(request.userAnswers))
  }
}