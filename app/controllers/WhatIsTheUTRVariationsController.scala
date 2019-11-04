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
import connector.{EnrolmentStoreConnector, TrustsStoreConnector}
import controllers.actions._
import forms.WhatIsTheUTRFormProvider
import javax.inject.Inject
import models.AgentTrustsResponse.NotClaimed
import models.Mode
import pages.WhatIsTheUTRVariationPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.{TrustLockedView, WhatIsTheUTRView}

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
                                                  trustsStoreConnector: TrustsStoreConnector,
                                                  enrolmentStoreConnector: EnrolmentStoreConnector
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

          val validateUtrAndRedirect = for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhatIsTheUTRVariationPage, value))
            _              <- sessionRepository.set(updatedAnswers)
            claim       <- trustsStoreConnector.get(request.internalId, value)
          } yield {

            claim match {
              case Some(c) =>
                if(c.trustLocked) {
                  Redirect(routes.WhatIsTheUTRVariationsController.trustStillLocked(draftId))
                } else {
                  Redirect(routes.TrustStatusController.status(draftId))
                }
              case _ => Redirect(routes.TrustStatusController.status(draftId))
            }

          }

          request.affinityGroup match {
            case Agent => enrolmentStoreConnector.getAgentTrusts(value) flatMap {
              case NotClaimed => Future.successful(Redirect(routes.TrustNotClaimedController.onPageLoad(draftId)))
              case _ => validateUtrAndRedirect
            }
            case _ => validateUtrAndRedirect
          }

        }
      )
  }


  def trustStillLocked(draftId: String) : Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>
      request.userAnswers.get(WhatIsTheUTRVariationPage) map {
        utr => {
          Future.successful(Ok(lockedView(utr)))
        }
      } getOrElse Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
  }

}
