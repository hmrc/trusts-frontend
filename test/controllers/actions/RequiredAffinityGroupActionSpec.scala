/*
 * Copyright 2024 HM Revenue & Customs
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
import controllers.register.routes._
import models.requests.IdentifierRequest
import org.scalatest.EitherValues
import org.scalatest.concurrent.ScalaFutures
import org.mockito.MockitoSugar
import play.api.http.HeaderNames
import play.api.mvc.Result
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, Enrolments}

import scala.concurrent.Future

  class RequiredAffinityGroupActionSpec extends RegistrationSpecBase with MockitoSugar with ScalaFutures with EitherValues {

    class Harness[T]()
      extends RequiredAgentAffinityGroupAction() {
      def callFilter[A](request: IdentifierRequest[A]): Future[Option[Result]] = filter(request)
    }

    "Required Affinity Group Action" when {

      "Affinity Group is Agent" must {

        "continue with returning None" in {

          val action = new Harness()
          val futureResult = action.callFilter(IdentifierRequest(fakeRequest,  "id", AffinityGroup.Agent, Enrolments(Set.empty[Enrolment])))

          whenReady(futureResult) { result =>
            result mustBe None
          }
        }

      }

      "Affinity Group is not Agent" must {

        "redirect to Unauthorised page" in {

          val action = new Harness()
          val futureResult = action.callFilter(IdentifierRequest(fakeRequest, "id", AffinityGroup.Organisation, Enrolments(Set.empty[Enrolment])))

          whenReady(futureResult) { result =>
            result.value.header.headers(HeaderNames.LOCATION) mustBe UnauthorisedController.onPageLoad().url
          }
        }

      }

    }

  }
