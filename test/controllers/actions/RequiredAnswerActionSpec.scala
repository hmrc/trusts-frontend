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
import controllers.routes
import models.NormalMode
import models.core.pages.FullName
import models.requests.RegistrationDataRequest
import org.scalatest.EitherValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import play.api.http.HeaderNames
import play.api.libs.json.Reads
import play.api.mvc.Result
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, Enrolments}
import controllers.register.routes._
import pages.register.trustees.individual.TrusteesNamePage

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RequiredAnswerActionSpec extends RegistrationSpecBase with MockitoSugar with ScalaFutures with EitherValues {

  class Harness[T](required: RequiredAnswer[T])(implicit val r: Reads[T])
    extends RequiredAnswerAction(required) {

    def callRefine[A](request: RegistrationDataRequest[A]): Future[Either[Result, RegistrationDataRequest[A]]] = refine(request)
  }

  "Required Answer Action" when {

      "there is no required answer" must {

        "redirect to Session Expired by default" in {
          val answers = emptyUserAnswers

          val action = new Harness(RequiredAnswer(TrusteesNamePage(0)))
          val futureResult = action.callRefine(new RegistrationDataRequest(fakeRequest, "id", answers, AffinityGroup.Organisation, Enrolments(Set.empty[Enrolment])))

          whenReady(futureResult) { result =>
            result.left.value.header.headers(HeaderNames.LOCATION) mustBe SessionExpiredController.onPageLoad().url
          }
        }

        "redirect to page specified" in {
          val answers = emptyUserAnswers

          val action = new Harness(RequiredAnswer(
            TrusteesNamePage(0),
            controllers.register.trustees.individual.routes.TrusteesNameController.onPageLoad(NormalMode, 0, fakeDraftId))
          )

          val futureResult = action.callRefine(new RegistrationDataRequest(fakeRequest, "id", answers, AffinityGroup.Organisation, Enrolments(Set.empty[Enrolment])))

          whenReady(futureResult) { result =>
            result.left.value.header.headers(HeaderNames.LOCATION) mustBe
              controllers.register.trustees.individual.routes.TrusteesNameController.onPageLoad(NormalMode, 0, fakeDraftId).url
          }
        }

      }

    "there is a required answer" must {

      "continue with refining the request" in {
        val answers = emptyUserAnswers.set(TrusteesNamePage(0), FullName("Adam", None, "Conder")).success.value

        val dataRequest = new RegistrationDataRequest(fakeRequest, "id", answers, AffinityGroup.Organisation, Enrolments(Set.empty[Enrolment]))

        val action = new Harness(RequiredAnswer(TrusteesNamePage(0)))
        val futureResult = action.callRefine(dataRequest)

        whenReady(futureResult) { result =>
          result.right.value mustEqual dataRequest
        }
      }

    }

  }

}
