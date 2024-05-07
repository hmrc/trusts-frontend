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

package connector

import com.github.tomakehurst.wiremock.client.WireMock._
import models.TaskStatuses
import models.registration.pages.TagStatus.Completed
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import org.scalatestplus.play.PlaySpec
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUserAnswers.draftId
import utils.WireMockHelper

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class TrustsStoreConnectorSpec extends PlaySpec with Matchers with OptionValues with WireMockHelper {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  lazy val app: Application = new GuiceApplicationBuilder()
    .configure(Seq(
      "microservice.services.trusts-store.port" -> server.port(),
      "auditing.enabled" -> false): _*
    ).build()

  private lazy val connector = app.injector.instanceOf[TrustsStoreConnector]

  "TrustsStoreConnector" when {

    ".getTaskStatuses" must {

      val url = s"/trusts-store/register/tasks/$draftId"

      "retrieve status for a draft" in {

        val taskStatuses = TaskStatuses(beneficiaries = Completed)

        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
                .withBody(Json.toJson(taskStatuses).toString)
            )
        )

        val result = Await.result(connector.getTaskStatuses(draftId), Duration.Inf)
        result mustEqual taskStatuses
      }
    }
  }
}
