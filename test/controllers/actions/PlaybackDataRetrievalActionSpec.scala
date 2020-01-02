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

package controllers.actions

import base.PlaybackSpecBase
import controllers.actions.playback.{OptionalPlaybackDataRequest, PlaybackDataRetrievalActionImpl}
import models.requests.IdentifierRequest
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import repositories.PlaybackRepository
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, Enrolments}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PlaybackDataRetrievalActionSpec extends PlaybackSpecBase with MockitoSugar with ScalaFutures {

  class Harness(repository: PlaybackRepository) extends PlaybackDataRetrievalActionImpl(repository) {
    def callTransform[A](request: IdentifierRequest[A]): Future[OptionalPlaybackDataRequest[A]] = transform(request)
  }

  "Data Retrieval Action" when {

    "there is no data in the cache" must {

      "set userAnswers to 'None' in the request" in {

        val repository = mock[PlaybackRepository]
        when(repository.get("internalId")).thenReturn(Future(None))
        val action = new Harness(repository)

        val futureResult = action.callTransform(new IdentifierRequest(fakeRequest, "internalId", AffinityGroup.Individual, Enrolments(Set.empty[Enrolment])))

        whenReady(futureResult) { result =>
          result.userAnswers.isEmpty mustBe true
        }
      }
    }

    "there is data in the cache" must {

      "build a userAnswers object and add it to the request" in {

        val repository = mock[PlaybackRepository]
        when(repository.get("internalId")).thenReturn(Future(Some(emptyUserAnswers)))
        val action = new Harness(repository)

        val futureResult = action.callTransform(new IdentifierRequest(fakeRequest, "internalId", AffinityGroup.Organisation, Enrolments(Set.empty[Enrolment])))

        whenReady(futureResult) { result =>
          result.userAnswers.isDefined mustBe true
        }
      }
    }
  }
}
