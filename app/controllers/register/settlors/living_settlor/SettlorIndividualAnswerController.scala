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

package controllers.register.settlors.living_settlor

import controllers.actions._
import controllers.actions.register._
import javax.inject.Inject
import models.NormalMode
import models.core.UserAnswers
import models.registration.pages.KindOfTrust.Employees
import models.registration.pages.Status.Completed
import navigation.Navigator
import pages.entitystatus.LivingSettlorStatus
import pages.register.settlors.living_settlor.trust_type.KindOfTrustPage
import pages.register.settlors.living_settlor.{SettlorIndividualAnswerPage, SettlorIndividualOrBusinessPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import services.DraftRegistrationService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import utils.annotations.LivingSettlor
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.register.settlors.living_settlor.SettlorIndividualAnswersView

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class SettlorIndividualAnswerController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   registrationsRepository: RegistrationsRepository,
                                                   draftRegistrationService: DraftRegistrationService,
                                                   @LivingSettlor navigator: Navigator,
                                                   identify: RegistrationIdentifierAction,
                                                   getData: DraftIdRetrievalActionProvider,
                                                   requireData: RegistrationDataRequiredAction,
                                                   requiredAnswer: RequiredAnswerActionProvider,
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

      val answers = new CheckYourAnswersHelper(countryOptions)(request.userAnswers, draftId, canEdit = true)

      val sections = Seq(
        AnswerSection(
          None,
          Seq(
            answers.setUpAfterSettlorDied,
            answers.kindOfTrust,
            answers.efrbsYesNo,
            answers.efrbsStartDate,
            answers.deedOfVariation,
            answers.holdoverReliefYesNo,
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

      Future.fromTry(answers) flatMap { updatedAnswers =>

        if(updatedAnswers.get(KindOfTrustPage).contains(Employees)){
          for {
            _ <- draftRegistrationService.setBeneficiaryStatus(draftId)
            _ <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(SettlorIndividualAnswerPage, NormalMode, draftId)(request.userAnswers))
        } else {
          registrationsRepository.set(updatedAnswers) map { _ =>
            Redirect(navigator.nextPage(SettlorIndividualAnswerPage, NormalMode, draftId)(request.userAnswers))
          }
        }

      }
  }

}
