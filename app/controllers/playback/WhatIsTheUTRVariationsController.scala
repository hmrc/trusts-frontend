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

package controllers.playback

import config.FrontendAppConfig
import connector.{TrustClaim, TrustsStoreConnector}
import controllers.actions._
import forms.WhatIsTheUTRFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import pages.WhatIsTheUTRVariationPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.RegistrationsRepository
import services.AuthenticationService
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.WhatIsTheUTRView

import scala.concurrent.{ExecutionContext, Future}

class WhatIsTheUTRVariationsController @Inject()(
                                                  override val messagesApi: MessagesApi,
                                                  registrationsRepository: RegistrationsRepository,
                                                  identify: IdentifierAction,
                                                  getData: DataRetrievalAction,
                                                  requireData: DataRequiredAction,
                                                  formProvider: WhatIsTheUTRFormProvider,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  view: WhatIsTheUTRView,
                                                  config: FrontendAppConfig,
                                                  trustsStore: TrustsStoreConnector,
                                                  errorHandler: ErrorHandler,
                                                  authenticationService: AuthenticationService
                                                )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhatIsTheUTRVariationPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, routes.WhatIsTheUTRVariationsController.onSubmit()))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, routes.WhatIsTheUTRVariationsController.onSubmit()))),
        value => {
          (for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhatIsTheUTRVariationPage, value))
            _ <- registrationsRepository.set(updatedAnswers)
            claim <- trustsStore.get(request.internalId, value)
          } yield claim) flatMap { claim =>

            lazy val handleTrustNotLocked = authenticationService.authenticate(value) map { redirect =>
              redirect.fold(
                result => result,
                _ => Redirect(routes.TrustStatusController.status())
              )
            }

            claim match {
              case Some(c) =>
                checkIfUTRLocked(handleTrustNotLocked, c)
              case _ =>
                handleTrustNotLocked
            }
          }
        }
      )
  }

  private def checkIfUTRLocked(onTrustNotLocked: => Future[Result], c: TrustClaim) = {
    if (c.trustLocked) {
      Future.successful(Redirect(routes.TrustStatusController.locked()))
    } else {
      onTrustNotLocked
    }
  }

}
