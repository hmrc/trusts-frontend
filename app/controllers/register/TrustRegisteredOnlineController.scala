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

package controllers.register

import controllers.actions.register.{MatchingAndSuitabilityDataRequiredAction, MatchingAndSuitabilityDataRetrievalAction, RegistrationIdentifierAction}
import forms.YesNoFormProvider
import models.requests.MatchingAndSuitabilityDataRequest
import navigation.Navigator
import pages.register.TrustRegisteredOnlinePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.CacheRepository
import services.FeatureFlagService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.TrustRegisteredOnlineView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TrustRegisteredOnlineController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 cacheRepository: CacheRepository,
                                                 navigator: Navigator,
                                                 identify: RegistrationIdentifierAction,
                                                 getData: MatchingAndSuitabilityDataRetrievalAction,
                                                 requireData: MatchingAndSuitabilityDataRequiredAction,
                                                 featureFlagService: FeatureFlagService,
                                                 formProvider: YesNoFormProvider,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 view: TrustRegisteredOnlineView
                                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions: ActionBuilder[MatchingAndSuitabilityDataRequest, AnyContent] = identify andThen getData andThen requireData

  private val form: Form[Boolean] = formProvider.withPrefix("trustRegisteredOnline")

  def onPageLoad(): Action[AnyContent] = actions {
    implicit request =>

      val preparedForm = request.userAnswers.get(TrustRegisteredOnlinePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm))
  }

  def onSubmit(): Action[AnyContent] = actions.async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TrustRegisteredOnlinePage, value))
            is5mldEnabled <- featureFlagService.is5mldEnabled()
            _ <- cacheRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(TrustRegisteredOnlinePage, is5mldEnabled = is5mldEnabled)(updatedAnswers))
        }
      )
  }
}
