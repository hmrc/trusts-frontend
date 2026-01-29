/*
 * Copyright 2024 HM Revenue & Customs
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
import navigation.Navigator
import pages.register.TrustHaveAUTRPage
import pages.register.suitability.{ExpressTrustYesNoPage, TrustTaxableYesNoPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.CacheRepository
import services.TrustsStoreService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.suitability.ExpressTrustYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ExpressTrustYesNoController @Inject() (
  override val messagesApi: MessagesApi,
  cacheRepository: CacheRepository,
  navigator: Navigator,
  actions: StandardActionSets,
  formProvider: YesNoFormProvider,
  featureFlagService: TrustsStoreService,
  val controllerComponents: MessagesControllerComponents,
  view: ExpressTrustYesNoView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = formProvider.withPrefix("suitability.expressTrust")

  def onPageLoad(): Action[AnyContent] = actions.identifiedUserMatchingAndSuitabilityData() { implicit request =>
    val preparedForm = request.userAnswers.get(ExpressTrustYesNoPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm))
  }

  def onSubmit(): Action[AnyContent] = actions.identifiedUserMatchingAndSuitabilityData().async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) => Future.successful(BadRequest(view(formWithErrors))),
        value =>
          for {
            answers        <- Future.fromTry(request.userAnswers.set(ExpressTrustYesNoPage, value))
            updatedAnswers <- {
              request.userAnswers.get(TrustHaveAUTRPage) match {
                case Some(true) => Future.fromTry(answers.set(TrustTaxableYesNoPage, true))
                case _          => Future(answers)
              }
            }
            _              <- cacheRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(ExpressTrustYesNoPage)(updatedAnswers))
      )
  }

}
