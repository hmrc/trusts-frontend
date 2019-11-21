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
import repositories.RegistrationsRepository
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
                                                  enrolmentStoreConnector: EnrolmentStoreConnector
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
            _              <- registrationsRepository.set(updatedAnswers)
            claim       <- trustsStore.get(request.internalId, value)
          } yield claim) flatMap { claim =>

            lazy val redirectTo = request.affinityGroup match {
              case Agent => enrolmentStoreConnector.getAgentTrusts(value) map {
                case NotClaimed => Redirect(routes.TrustNotClaimedController.onPageLoad())
                case _ =>

                  val agentEnrolled = request.enrolments.enrolments exists { enrolment =>
                    enrolment.key equals config.serviceName
                  }

                  if(agentEnrolled){
                    Redirect(routes.TrustStatusController.status())
                  } else {
                    Redirect(routes.AgentNotAuthorisedController.onPageLoad())
                  }

              }
              case _ => Future.successful(Redirect(routes.TrustStatusController.status()))
            }

            claim match {
              case Some(c) =>
                if(c.trustLocked) {
                  Future.successful(Redirect(routes.TrustStatusController.locked()))
                } else {
                  redirectTo
                }
              case _ => redirectTo
            }

          }

        }
      )
  }
}
