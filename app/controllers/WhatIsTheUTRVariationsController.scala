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

package controllers

import config.FrontendAppConfig
import connector.TrustsStoreConnector
import controllers.actions._
import forms.WhatIsTheUTRFormProvider
import javax.inject.Inject
import models.Mode
import pages.WhatIsTheUTRVariationPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.{WhatIsTheUTRView, TrustLockedView}

import scala.concurrent.{ExecutionContext, Future}

class WhatIsTheUTRVariationsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: SessionRepository,
                                        identify: IdentifierAction,
                                        getData: DraftIdRetrievalActionProvider,
                                        requireData: DataRequiredAction,
                                        formProvider: WhatIsTheUTRFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: WhatIsTheUTRView,
                                        lockedView: TrustLockedView,
                                        config: FrontendAppConfig,
                                        connector: TrustsStoreConnector
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhatIsTheUTRVariationPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId, routes.WhatIsTheUTRVariationsController.onSubmit(mode, draftId)))
  }

  def onSubmit(mode: Mode, draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId, routes.WhatIsTheUTRVariationsController.onSubmit(mode, draftId)))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhatIsTheUTRVariationPage, value))
            _              <- sessionRepository.set(updatedAnswers)
            response       <- connector.get(request.internalId)
          } yield {
            ((response \ "trustLocked").as[Boolean], (response \ "utr").as[String] == value) match {
              case (true, true) => Redirect(routes.WhatIsTheUTRVariationsController.trustStillLocked(draftId))
              case (_ , _) => Redirect(config.claimATrustUrl(value))
            }
          }
        }
      )
  }

  def trustStillLocked(draftId: String) : Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>
      request.userAnswers.get(WhatIsTheUTRVariationPage) map {
        utr => Future.successful(Ok(lockedView(utr)))
      } getOrElse Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
  }

}
