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

package controllers.register

import controllers.actions._
import forms.YesNoFormProvider
import pages.register.TrustRegisteredWithUkAddressYesNoPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.CacheRepository
import services.MatchingService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.TrustRegisteredWithUkAddressYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TrustRegisteredWithUkAddressYesNoController @Inject()(
                                                             override val messagesApi: MessagesApi,
                                                             cacheRepository: CacheRepository,
                                                             matchingService: MatchingService,
                                                             actions: StandardActionSets,
                                                             yesNoFormProvider: YesNoFormProvider,
                                                             val controllerComponents: MessagesControllerComponents,
                                                             view: TrustRegisteredWithUkAddressYesNoView
                                                           )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[Boolean] = yesNoFormProvider.withPrefix("trustRegisteredWithUkAddress")

  def onPageLoad(): Action[AnyContent] = actions.identifiedUserMatchingAndSuitabilityData() {
    implicit request =>

      val preparedForm = request.userAnswers.get(TrustRegisteredWithUkAddressYesNoPage) match {
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
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TrustRegisteredWithUkAddressYesNoPage, value))
            _ <- cacheRepository.set(updatedAnswers)
            redirect <- {
              if (value) {
                Future.successful(Redirect(routes.PostcodeForTheTrustController.onPageLoad()))
              } else {
                matchingService.matching(updatedAnswers, request.isAgent)
              }
            }
          } yield redirect
        }
      )
  }
}
