/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.register.asset.property_or_land

import controllers.actions._
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import javax.inject.Inject
import models.NormalMode
import models.registration.pages.Status.Completed
import models.requests.RegistrationDataRequest
import navigation.Navigator
import pages.entitystatus.AssetStatus
import pages.register.asset.property_or_land.{PropertyOrLandAddressYesNoPage, PropertyOrLandAnswerPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.annotations.PropertyOrLand
import utils.countryOptions.CountryOptions
import utils.{CheckYourAnswersHelper, DateFormatter}
import viewmodels.AnswerSection
import views.html.register.asset.property_or_land.PropertyOrLandAnswersView

import scala.concurrent.{ExecutionContext, Future}

class PropertyOrLandAnswerController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                registrationsRepository: RegistrationsRepository,
                                                @PropertyOrLand navigator: Navigator,
                                                identify: RegistrationIdentifierAction,
                                                getData: DraftIdRetrievalActionProvider,
                                                requireData: RegistrationDataRequiredAction,
                                                requiredAnswer: RequiredAnswerActionProvider,
                                                view: PropertyOrLandAnswersView,
                                                countryOptions: CountryOptions,
                                                val controllerComponents: MessagesControllerComponents,
                                                dateFormatter: DateFormatter
                                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(index: Int, draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    identify andThen
      getData(draftId) andThen
      requireData andThen
      requiredAnswer(RequiredAnswer(PropertyOrLandAddressYesNoPage(index), routes.PropertyOrLandAddressYesNoController.onPageLoad(NormalMode, index, draftId)))


  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val answers = new CheckYourAnswersHelper(countryOptions, dateFormatter)(request.userAnswers, draftId, canEdit = true)

      val sections = Seq(
        AnswerSection(
          None,
          Seq(
            answers.whatKindOfAsset(index),
            answers.propertyOrLandAddressYesNo(index),
            answers.propertyOrLandAddressUkYesNo(index),
            answers.propertyOrLandUKAddress(index),
            answers.propertyOrLandInternationalAddress(index),
            answers.propertyOrLandDescription(index),
            answers.propertyOrLandTotalValue(index),
            answers.trustOwnAllThePropertyOrLand(index),
            answers.propertyLandValueTrust(index)
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
      } yield Redirect(navigator.nextPage(PropertyOrLandAnswerPage, NormalMode, draftId)(request.userAnswers))

  }
}
