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

package controllers.register.suitability

import controllers.actions.StandardActionSets
import forms.YesNoFormProvider

import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.register.suitability.{TrustTaxableYesNoPage, UndeclaredTaxLiabilityYesNoPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import services.FeatureFlagService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.suitability.UndeclaredTaxLiabilityYesNoView

import scala.concurrent.{ExecutionContext, Future}

class UndeclaredTaxLiabilityYesNoController @Inject()(
                                                       override val messagesApi: MessagesApi,
                                                       registrationsRepository: RegistrationsRepository,
                                                       standardActionSets: StandardActionSets,
                                                       navigator: Navigator,
                                                       yesNoFormProvider: YesNoFormProvider,
                                                       featureFlagService: FeatureFlagService,
                                                       val controllerComponents: MessagesControllerComponents,
                                                       view: UndeclaredTaxLiabilityYesNoView
                                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = yesNoFormProvider.withPrefix("suitability.undeclaredTaxLiability")

  private def actions(draftId: String) =
    standardActionSets.identifiedUserWithData(draftId)

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(UndeclaredTaxLiabilityYesNoPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId))
  }

  def onSubmit(mode: Mode, draftId : String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId))),

        value => {
          for {
            answers <- Future.fromTry(request.userAnswers.set(UndeclaredTaxLiabilityYesNoPage, value))
            updatedAnswers <- Future.fromTry(answers.set(TrustTaxableYesNoPage, value))
            is5mld <- featureFlagService.is5mldEnabled()
            _ <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(UndeclaredTaxLiabilityYesNoPage, mode, draftId, is5mldEnabled = is5mld)(updatedAnswers))
        }
      )
  }
}
