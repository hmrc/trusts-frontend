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

import java.time.format.DateTimeFormatter

import config.FrontendAppConfig
import controllers.actions._
import javax.inject.Inject
import models.Matched.{AlreadyRegistered, Failed, Success}
import models.NormalMode
import models.RegistrationProgress.InProgress
import pages._
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.TaskListView

import scala.concurrent.{ExecutionContext, Future}

class TaskListController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       requiredAnswer: RequiredAnswerActionProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: TaskListView,
                                       config: FrontendAppConfig,
                                       registrationProgress: RegistrationProgress,
                                       sessionRepository: SessionRepository
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  private def actions =
    identify andThen getData andThen requireData andThen
      requiredAnswer(
        RequiredAnswer(
          TrustRegisteredOnlinePage, routes.TrustRegisteredOnlineController.onPageLoad(NormalMode))
      ) andThen
      requiredAnswer(
        RequiredAnswer(
          TrustHaveAUTRPage, routes.TrustHaveAUTRController.onPageLoad(NormalMode))
      )

  def onPageLoad: Action[AnyContent] = actions.async {
    implicit request =>

      def renderView(affinityGroup : AffinityGroup) = {
        val ttlInSeconds = config.ttlInSeconds
        val savedUntil = request.userAnswers.createdAt.plusSeconds(ttlInSeconds).format(dateFormatter)

        val updatedAnswers = request.userAnswers.copy(progress = InProgress)

        for {
          _              <- sessionRepository.set(updatedAnswers)
        } yield {

          val sections = registrationProgress.sections(updatedAnswers)

          Logger.debug(s"[TaskList][sections] $sections")

          Ok(view(savedUntil, sections, affinityGroup))
        }
      }

      val isExistingTrust = request.userAnswers.get(TrustHaveAUTRPage).get

      if (isExistingTrust) {
        request.userAnswers.get(ExistingTrustMatched) match {
          case Some(Success) =>
            renderView(request.affinityGroup)

          case Some(AlreadyRegistered) | Some(Failed) =>
            Future.successful(Redirect(routes.FailedMatchController.onPageLoad().url))

          case None =>
            Future.successful(Redirect(routes.WhatIsTheUTRController.onPageLoad(NormalMode).url))
        }
      } else {
        renderView(request.affinityGroup)
      }
  }
}
