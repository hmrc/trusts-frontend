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
import connector.{EnrolmentStoreConnector, TrustClaim, TrustsStoreConnector}
import controllers.actions._
import forms.WhatIsTheUTRFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import models.EnrolmentStoreResponse.{AlreadyClaimed, NotClaimed}
import models.requests.DataRequest
import pages.WhatIsTheUTRVariationPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.RegistrationsRepository
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.http.HeaderCarrier
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
                                                  enrolmentStoreConnector: EnrolmentStoreConnector,
                                                  errorHandler: ErrorHandler
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

            lazy val handleTrustNotLocked = request.affinityGroup match {
              case Agent => checkIfAgentAuthorised(value)
              case _ => checkIfTrustIsNotClaimed(value)
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

  private def checkIfTrustIsNotClaimed(utr: String)(implicit hc : HeaderCarrier, request: DataRequest[AnyContent]) = {
    enrolmentStoreConnector.checkIfClaimed(utr) map {
      case AlreadyClaimed => Redirect(routes.TrustStatusController.alreadyClaimed())
      case NotClaimed => Redirect(routes.TrustStatusController.status())
      case _ => InternalServerError(errorHandler.internalServerErrorTemplate)
    }
  }

  private def checkIfAgentAuthorised(utr: String)(implicit hc : HeaderCarrier, request: DataRequest[AnyContent]) = {
    enrolmentStoreConnector.checkIfClaimed(utr) map {
      case NotClaimed => Redirect(routes.TrustNotClaimedController.onPageLoad())
      case _ =>

        val agentEnrolled = checkEnrolmentOfAgent(utr)

        if (agentEnrolled) {
          Redirect(routes.TrustStatusController.status())
        } else {
          Redirect(routes.AgentNotAuthorisedController.onPageLoad())
        }
    }
  }

  private def checkEnrolmentOfAgent(utr: String)(implicit request: DataRequest[AnyContent]) = {
    request.enrolments.enrolments
      .find(_.key equals config.serviceName)
      .flatMap(_.identifiers.find(_.key equals "SAUTR"))
      .exists(_.value equals utr)
  }

}
