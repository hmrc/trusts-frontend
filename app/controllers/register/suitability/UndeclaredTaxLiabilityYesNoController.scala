/*
 * Copyright 2022 HM Revenue & Customs
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
import pages.register.suitability.{TrustTaxableYesNoPage, UndeclaredTaxLiabilityYesNoPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.CacheRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.suitability.UndeclaredTaxLiabilityYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UndeclaredTaxLiabilityYesNoController @Inject()(
                                                       override val messagesApi: MessagesApi,
                                                       cacheRepository: CacheRepository,
                                                       actions: StandardActionSets,
                                                       navigator: Navigator,
                                                       yesNoFormProvider: YesNoFormProvider,
                                                       val controllerComponents: MessagesControllerComponents,
                                                       view: UndeclaredTaxLiabilityYesNoView
                                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[Boolean] = yesNoFormProvider.withPrefix("suitability.undeclaredTaxLiability")

  def onPageLoad(): Action[AnyContent] = actions.identifiedUserMatchingAndSuitabilityData() {
    implicit request =>

      val preparedForm = request.userAnswers.get(UndeclaredTaxLiabilityYesNoPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm))
  }

  def onSubmit(): Action[AnyContent] = actions.identifiedUserMatchingAndSuitabilityData().async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors))),

        value => {
          for {
            answers <- Future.fromTry(request.userAnswers.set(UndeclaredTaxLiabilityYesNoPage, value))
            updatedAnswers <- Future.fromTry(answers.set(TrustTaxableYesNoPage, value))
            _ <- cacheRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(UndeclaredTaxLiabilityYesNoPage)(updatedAnswers))
        }
      )
  }
}
