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

import base.RegistrationSpecBase
import controllers.actions.register.RegistrationDataRetrievalActionImpl
import models.requests.{IdentifierRequest, OptionalRegistrationDataRequest}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import repositories.RegistrationsRepository
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, Enrolments}
import viewmodels.DraftRegistration

import scala.concurrent.Future

class RegistrationDataRetrievalActionSpec extends RegistrationSpecBase with MockitoSugar with ScalaFutures {

  class Harness(registrationsRepository: RegistrationsRepository) extends RegistrationDataRetrievalActionImpl(registrationsRepository) {
    def callTransform[A](request: IdentifierRequest[A]): Future[OptionalRegistrationDataRequest[A]] = transform(request)
  }

  "Data Retrieval Action" when {

    "there is no data in the cache" must {

      "set userAnswers to 'None' in the request" in {

        val registrationsRepository = mock[RegistrationsRepository]
        when(registrationsRepository.listDrafts()(any())) thenReturn Future(Nil)
        val action = new Harness(registrationsRepository)

        val futureResult = action.callTransform(IdentifierRequest(fakeRequest, "internalId", AffinityGroup.Individual, Enrolments(Set.empty[Enrolment])))

        whenReady(futureResult) { result =>
          result.userAnswers.isEmpty mustBe true
        }
      }
    }

    "there is data in the cache" must {

      "build a userAnswers object and add it to the request" in {

        val registrationsRepository = mock[RegistrationsRepository]
        val draftRegistration = DraftRegistration("draftId", Some("reference"), "saved-until-date")
        when(registrationsRepository.listDrafts()(any())) thenReturn Future(List(draftRegistration))
        when(registrationsRepository.get(draftId = any())(any())) thenReturn Future(Some(emptyUserAnswers))
        val action = new Harness(registrationsRepository)

        val futureResult = action.callTransform(IdentifierRequest(fakeRequest, "internalId", AffinityGroup.Individual, Enrolments(Set.empty[Enrolment])))

        whenReady(futureResult) { result =>
          result.userAnswers.isDefined mustBe true
        }
      }

      "set userAnswers to 'None' because 'get' query returns 'None'" in {
        val registrationsRepository = mock[RegistrationsRepository]
        val draftRegistration = DraftRegistration("draftId", Some("reference"), "saved-until-date")

        when(registrationsRepository.listDrafts()(any())) thenReturn Future(List(draftRegistration))
        when(registrationsRepository.get(draftId = any())(any())) thenReturn Future(None)

        val action = new Harness(registrationsRepository)

        val futureResult = action.callTransform(IdentifierRequest(fakeRequest, "internalId", AffinityGroup.Individual, Enrolments(Set.empty[Enrolment])))

        whenReady(futureResult) { result =>
          result.userAnswers.isEmpty mustBe true
        }
      }
    }
  }
}
