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

package connector

import com.github.tomakehurst.wiremock.client.WireMock._
import config.FrontendAppConfig
import models.EnrolmentsResponse.{AgentTrusts, NoTrusts}
import org.scalatest.{AsyncFreeSpec, MustMatchers}
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.http.HeaderCarrier
import utils.WireMockHelper

import scala.concurrent.ExecutionContext.Implicits.global

class EnrolmentStoreConnectorSpec extends AsyncFreeSpec with MustMatchers with WireMockHelper {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private def wiremock(expectedStatus: Int, expectedResponse: Option[String]) = {

    val response = expectedResponse map { response =>
      aResponse()
        .withStatus(expectedStatus)
        .withBody(response)
    } getOrElse {
      aResponse()
        .withStatus(expectedStatus)
    }

    server.stubFor(get(urlEqualTo(enrolmentsUrl)).willReturn(response))

  }

  lazy val app: Application = new GuiceApplicationBuilder()
    .configure(Seq(
      "microservice.services.enrolment-store-proxy.port" -> server.port(),
      "auditing.enabled" -> false): _*
    ).build()

  private lazy val connector = app.injector.instanceOf[EnrolmentStoreConnector]
  private lazy val config = app.injector.instanceOf[FrontendAppConfig]

  private lazy val serviceName = config.serviceName

  private val identifierKey = "UTR"
  private val identifier = "0987654321"

  private val principalId = Seq("ABCEDEFGI1234567")
  private val delegatedId = Seq("ABCEDEFGI1234568", "ABCEDEFGI1234569")

  private lazy val enrolmentsUrl: String = s"/enrolment-store/enrolments/$serviceName~$identifierKey~$identifier/users"

  "EnrolmentStoreConnector" - {
    "must get a list of enrolments and delegated enrolments when" - {

      "valid enrolment key retrieves a Success 200 with a body of enrolments" in {

        wiremock(
          expectedStatus = Status.OK,
          expectedResponse = Some(
            s"""{
              |    "principalUserIds": [
              |       "${principalId.head}"
              |    ],
              |    "delegatedUserIds": [
              |       "${delegatedId.head}",
              |       "${delegatedId.last}"
              |    ]
              |}""".stripMargin
        ))

        connector.getAgentTrusts(identifier) map { result =>
          result mustBe AgentTrusts(principalId, delegatedId)
        }

      }

    }

    "No Trusts when" - {

      "valid enrolment key retrieves a No Content 204" in {

        wiremock(
          expectedStatus = Status.NO_CONTENT,
          expectedResponse = None
        )

        connector.getAgentTrusts(identifier) map { result =>
          result mustBe NoTrusts
        }

      }

    }

  }

}
