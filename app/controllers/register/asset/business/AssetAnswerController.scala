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

package controllers.register.asset.business

import controllers.actions._
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import controllers.filters.IndexActionFilterProvider
import javax.inject.Inject
import models.NormalMode
import models.registration.pages.Status.Completed
import navigation.Navigator
import pages.entitystatus.AssetStatus
import pages.register.asset.business.AssetNamePage
import pages.register.asset.shares.ShareAnswerPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.register.asset.buisness.AssetAnswerView

import scala.concurrent.{ExecutionContext, Future}

class AssetAnswerController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       registrationsRepository: RegistrationsRepository,
                                       navigator: Navigator,
                                       identify: RegistrationIdentifierAction,
                                       getData: DraftIdRetrievalActionProvider,
                                       requireData: RegistrationDataRequiredAction,
                                       requiredAnswer: RequiredAnswerActionProvider,
                                       validateIndex: IndexActionFilterProvider,
                                       view: AssetAnswerView,
                                       countryOptions: CountryOptions,
                                       val controllerComponents: MessagesControllerComponents
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(index: Int, draftId: String) =
    identify andThen
      getData(draftId) andThen
      requireData andThen
      requiredAnswer(RequiredAnswer(AssetNamePage(index), routes.AssetNameController.onPageLoad(NormalMode, index, draftId)))


  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val answers = new CheckYourAnswersHelper(countryOptions)(request.userAnswers, draftId, canEdit = true)

      val sections = Seq(
        AnswerSection(
          None,
          Seq(
            answers.assetAddressUkYesNo(index),
            answers.assetDescription(index),
            answers.assetInternationalAddress(index),
            answers.assetNamePage(index),
            answers.assetUkAddress(index),
            answers.currentValue(index)
          ).flatten
        )
      )


      Ok(view(index, draftId, sections))
  }


  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val answers = request.userAnswers.set(AssetStatus(index), Completed)

      for {
        updatedAnswers <- Future.fromTry(answers)
        _ <- registrationsRepository.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(ShareAnswerPage, NormalMode, draftId)(request.userAnswers))

  }
}
