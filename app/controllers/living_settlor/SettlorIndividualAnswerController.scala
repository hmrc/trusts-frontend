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

package controllers.living_settlor

import controllers.actions._
import controllers.filters.IndexActionFilterProvider
import javax.inject.Inject
import models.NormalMode
import models.Status.Completed
import navigation.Navigator
import pages.entitystatus.LivingSettlorStatus
import pages.living_settlor.{SettlorIndividualAnswerPage, SettlorIndividualOrBusinessPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import utils.annotations.LivingSettlor
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.living_settlor.SettlorIndividualAnswersView

import scala.concurrent.{ExecutionContext, Future}

class SettlorIndividualAnswerController @Inject()(
                                      override val messagesApi: MessagesApi,
                                      sessionRepository: SessionRepository,
                                      @LivingSettlor navigator: Navigator,
                                      identify: IdentifierAction,
                                      getData: DraftIdRetrievalActionProvider,
                                      requireData: DataRequiredAction,
                                      requiredAnswer: RequiredAnswerActionProvider,
                                      validateIndex: IndexActionFilterProvider,
                                      view: SettlorIndividualAnswersView,
                                      countryOptions: CountryOptions,
                                      val controllerComponents: MessagesControllerComponents
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(index: Int, draftId: String) =
    identify andThen
      getData(draftId) andThen
      requireData andThen
      requiredAnswer(RequiredAnswer(SettlorIndividualOrBusinessPage(index), routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, index, draftId)))


  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val answers = new CheckYourAnswersHelper(countryOptions)(request.userAnswers, draftId)

      val sections = Seq(
        AnswerSection(
          None,
          Seq(
            answers.settlorKindOfTrust,
            answers.settlorHandoverReliefYesNo,
            answers.settlorIndividualOrBusiness(index),
            answers.settlorIndividualName(index),
            answers.settlorIndividualDateOfBirthYesNo(index),
            answers.settlorIndividualDateOfBirth(index),
            answers.settlorIndividualNINOYesNo(index),
            answers.settlorIndividualNINO(index),
            answers.settlorIndividualAddressYesNo(index),
            answers.settlorIndividualAddressUKYesNo(index),
            answers.settlorIndividualAddressUK(index),
            answers.settlorIndividualAddressInternational(index),
            answers.settlorIndividualPassportYesNo(index),
            answers.settlorIndividualPassport(index),
            answers.settlorIndividualIDCardYesNo(index),
            answers.settlorIndividualIDCard(index)
          ).flatten
        )
      )

      Ok(view(index, draftId, sections))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val answers = request.userAnswers.set(LivingSettlorStatus(index), Completed)

      for {
        updatedAnswers <- Future.fromTry(answers)
        _ <- sessionRepository.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(SettlorIndividualAnswerPage, NormalMode, draftId)(request.userAnswers))

  }
}
