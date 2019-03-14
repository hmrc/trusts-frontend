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
import models.NormalMode
import pages.{ExistingTrustMatched, TrustHaveAUTRPage, TrustRegisteredOnlinePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import viewmodels.{Complete, InProgress, Task}
import views.html.TaskListView

import scala.concurrent.ExecutionContext

class TaskListController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       requiredAnswer: RequiredAnswerActionProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: TaskListView,
                                       config: FrontendAppConfig
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

  def onPageLoad: Action[AnyContent] = actions {
    implicit request =>

      val ttlInSeconds = config.ttlInSeconds
      val savedUntil = request.userAnswers.createdAt.plusSeconds(ttlInSeconds).format(dateFormatter)

      val isExistingTrust = request.userAnswers.get(TrustHaveAUTRPage).get

      val sections = List(
        Task("trust-details", routes.AddATrusteeController.onPageLoad(), Some(Complete)),
        Task("settlors", routes.AddATrusteeController.onPageLoad(), Some(InProgress)),
        Task("trustees", routes.AddATrusteeController.onPageLoad(), Some(InProgress)),
        Task("beneficiaries", routes.AddATrusteeController.onPageLoad(), None),
        Task("assets", routes.AddATrusteeController.onPageLoad(), None),
        Task("tax-liability", routes.AddATrusteeController.onPageLoad(), Some(Complete))
      )

      if (isExistingTrust) {
        request.userAnswers.get(ExistingTrustMatched) match {
          case Some(true) =>
            Ok(view(savedUntil, sections))
          case Some(false) =>
            Redirect(routes.FailedMatchController.onPageLoad().url)
          case None =>
            Redirect(routes.WhatIsTheUTRController.onPageLoad(NormalMode).url)
        }
      } else {
        Ok(view(savedUntil, sections))
      }
  }
}
