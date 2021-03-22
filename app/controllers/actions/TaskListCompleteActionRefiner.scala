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

package controllers.actions

import models.requests.RegistrationDataRequest
import pages.register.RegistrationProgress
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TaskListCompleteActionRefinerImpl @Inject()(
                                                   registrationProgress: RegistrationProgress,
                                                   implicit val executionContext: ExecutionContext
                                                 ) extends TaskListCompleteActionRefiner {

  override protected def refine[A](request: RegistrationDataRequest[A]): Future[Either[Result, RegistrationDataRequest[A]]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    registrationProgress.isTaskListComplete(
      draftId = request.userAnswers.draftId,
      isTaxable = request.userAnswers.isTaxable
    ) map {
      case true => Right(request)
      case false => Left(Redirect(controllers.register.routes.TaskListController.onPageLoad(request.userAnswers.draftId)))
    }
  }
}

trait TaskListCompleteActionRefiner extends ActionRefiner[RegistrationDataRequest, RegistrationDataRequest]
