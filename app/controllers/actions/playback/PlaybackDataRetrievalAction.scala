/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.actions.playback

import javax.inject.Inject
import models.playback.UserAnswers
import models.requests.IdentifierRequest
import play.api.mvc.ActionTransformer
import repositories.PlaybackRepository

import scala.concurrent.{ExecutionContext, Future}

class PlaybackDataRetrievalActionImpl @Inject()(val playbackRepository: PlaybackRepository)
                                                   (implicit val executionContext: ExecutionContext) extends PlaybackDataRetrievalAction {

  override protected def transform[A](request: IdentifierRequest[A]): Future[OptionalPlaybackDataRequest[A]] = {

    def createdOptionalDataRequest(request: IdentifierRequest[A], userAnswers: Option[UserAnswers]) =
      OptionalPlaybackDataRequest(
        request.request,
        request.identifier,
        userAnswers,
        request.affinityGroup,
        request.enrolments,
        request.agentARN
      )

    playbackRepository.get(request.identifier) map {
      case None =>
        createdOptionalDataRequest(request, None)
      case Some(userAnswers) =>
        createdOptionalDataRequest(request, Some(userAnswers))
    }
  }
}

trait PlaybackDataRetrievalAction extends ActionTransformer[IdentifierRequest, OptionalPlaybackDataRequest]
