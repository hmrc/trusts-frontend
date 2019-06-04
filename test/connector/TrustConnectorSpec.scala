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

import base.SpecBaseHelpers
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, containing, equalTo, post, urlEqualTo}
import generators.Generators
import mapping.{Mapping, Registration, RegistrationMapper}
import models.{RegistrationTRNResponse, TrustResponse}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.CONTENT_TYPE
import utils.{TestUserAnswers, WireMockHelper}

import scala.concurrent.Future

class TrustConnectorSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers with WireMockHelper with ScalaFutures {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      Seq("microservice.services.trusts.port" -> server.port(),
        "auditing.enabled" -> false): _*).build()

  private lazy val registrationMapper: Mapping[Registration] = injector.instanceOf[RegistrationMapper]

  private lazy val connector = injector.instanceOf[TrustConnector]

  private val url : String = "/trusts/register"

  private def wiremock(payload: String, expectedStatus: Int, expectedResponse : String)=
    server.stubFor(
      post(urlEqualTo(url))
    .withHeader(CONTENT_TYPE, containing("application/json"))
    .withRequestBody(equalTo(payload))
    .willReturn(
      aResponse()
        .withStatus(expectedStatus)
        .withBody(expectedResponse)
      )
    )

  private val newTrustUserAnswers = {
    val emptyUserAnswers = TestUserAnswers.emptyUserAnswers
    val uaWithLead = TestUserAnswers.withLeadTrustee(emptyUserAnswers)
    val uaWithDeceased = TestUserAnswers.withDeceasedSettlor(uaWithLead)
    val uaWithIndBen = TestUserAnswers.withIndividualBeneficiary(uaWithDeceased)
    val uaWithTrustDetails = TestUserAnswers.withTrustDetails(uaWithIndBen)
    val asset = TestUserAnswers.withMoneyAsset(uaWithTrustDetails)
    val userAnswers = TestUserAnswers.withDeclaration(asset)

    userAnswers
  }

  "TrustConnector" - {

    "return a Trust Registration Number (TRN)" - {

      "valid payload to trusts is sent" in {

        val registration = registrationMapper.build(newTrustUserAnswers).value

        val payload = Json.stringify(Json.toJson(registration))

        wiremock(
          payload = payload,
          expectedStatus = Status.OK,
          expectedResponse = """
            |{
            | "trn": "XTRN1234567"
            |}
          """.stripMargin
        )

        val futureResult : Future[TrustResponse] = connector.register(registration)

        whenReady(futureResult) {
          result =>
            result mustBe RegistrationTRNResponse("XTRN1234567")
        }
      }

    }

  }

}
