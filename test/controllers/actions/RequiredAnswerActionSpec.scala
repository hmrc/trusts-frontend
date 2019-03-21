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

package controllers.actions

import base.SpecBase
import controllers.routes
import models.{FullName, NormalMode, UserAnswers}
import models.requests.DataRequest
import org.scalatest.EitherValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import pages.TrusteesNamePage
import play.api.http.HeaderNames
import play.api.libs.json.Reads
import play.api.mvc.Result
import uk.gov.hmrc.auth.core.AffinityGroup

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RequiredAnswerActionSpec extends SpecBase with MockitoSugar with ScalaFutures with EitherValues {

  class Harness[T](required: RequiredAnswer[T])(implicit val r: Reads[T])
    extends RequiredAnswerAction(required) {

    def callRefine[A](request: DataRequest[A]): Future[Either[Result, DataRequest[A]]] = refine(request)
  }


  "Required Answer Action" when {

      "there is no required answer" must {

        "redirect to Session Expired by default" in {
          val answers = emptyUserAnswers

          val action = new Harness(RequiredAnswer(TrusteesNamePage(0)))
          val futureResult = action.callRefine(new DataRequest(fakeRequest, "id", answers, AffinityGroup.Organisation))

          whenReady(futureResult) { result =>
            result.left.value.header.headers(HeaderNames.LOCATION) mustBe routes.SessionExpiredController.onPageLoad().url
          }
        }

        "redirect to page specified" in {
          val answers = emptyUserAnswers

          val action = new Harness(RequiredAnswer(TrusteesNamePage(0), routes.TrusteesNameController.onPageLoad(NormalMode, 0)))
          val futureResult = action.callRefine(new DataRequest(fakeRequest, "id", answers, AffinityGroup.Organisation))

          whenReady(futureResult) { result =>
            result.left.value.header.headers(HeaderNames.LOCATION) mustBe routes.TrusteesNameController.onPageLoad(NormalMode, 0).url
          }
        }

      }

    "there is a required answer" must {

      "continue with refining the request" in {
        val answers = UserAnswers("id").set(TrusteesNamePage(0), FullName("Adam", None, "Conder")).success.value

        val dataRequest = new DataRequest(fakeRequest, "id", answers, AffinityGroup.Organisation)

        val action = new Harness(RequiredAnswer(TrusteesNamePage(0)))
        val futureResult = action.callRefine(dataRequest)

        whenReady(futureResult) { result =>
          result.right.value mustEqual dataRequest
        }
      }

    }

  }

}
