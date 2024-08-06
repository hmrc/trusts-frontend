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

import base.SpecBaseHelpers
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import mapping.registration.RegistrationMapper
import models.core.http._
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.CONTENT_TYPE
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.http.HeaderCarrier
import utils.{TestUserAnswers, WireMockHelper}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class TrustConnectorSpec extends AnyFreeSpec with Matchers with OptionValues with SpecBaseHelpers with WireMockHelper {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(Seq(
      "microservice.services.trusts.port" -> server.port(),
      "auditing.enabled" -> false): _*
    ).build()

  private lazy val connector = injector.instanceOf[TrustConnector]

  private lazy val registrationMapper: RegistrationMapper = injector.instanceOf[RegistrationMapper]

  def wiremock(payload: String, expectedStatus: Int, expectedResponse: String, url: String): StubMapping =
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

  val trustName = "Trust Name"

  "TrustConnector" - {

    ".register" - {

      val registerUrl: String = "/trusts/register"

      val newTrustUserAnswers = {
        val emptyUserAnswers = TestUserAnswers.emptyUserAnswers
        val userAnswers = TestUserAnswers.withDeclaration(emptyUserAnswers)

        userAnswers
      }

      val correspondenceAddress = AddressType("line1", "line2", None, None, Some("AA1 1AA"), "GB")

      "return a Trust Registration Number (TRN)" - {

        "valid payload to trusts is sent" in {

          val registration = Await.result(registrationMapper.build(newTrustUserAnswers, correspondenceAddress, trustName, Organisation), Duration.Inf).value

          val payload = Json.stringify(Json.toJson(registration))

          wiremock(
            payload = payload,
            expectedStatus = Status.OK,
            expectedResponse =
              """
                |{
                | "trn": "XTRN1234567"
                |}
          """.stripMargin,
            url = registerUrl
          )

          val result = Await.result(connector.register(Json.toJson(registration), TestUserAnswers.draftId), Duration.Inf)
          result mustBe RegistrationTRNResponse("XTRN1234567")
        }
      }

      "return AlreadyRegistered response " - {

        "already registered trusts is sent " in {
          val userAnswers = TestUserAnswers.withMatchingSuccess(newTrustUserAnswers)
          val registration = Await.result(registrationMapper.build(userAnswers, correspondenceAddress, trustName, Organisation), Duration.Inf).value

          val payload = Json.stringify(Json.toJson(registration))

          wiremock(
            payload = payload,
            expectedStatus = Status.CONFLICT,
            expectedResponse =
              """
                |{
                | "code": "ALREADY_REGISTERED",
                |  "message": "Trusts already registered."
                |}
                             """.stripMargin,
            url = registerUrl
          )

          val result = Await.result(connector.register(Json.toJson(registration), TestUserAnswers.draftId), Duration.Inf)
          result mustBe TrustResponse.AlreadyRegistered
        }
      }

      "return InternalServerError response " - {
        "api returns internal server error response " in {
          val userAnswers = TestUserAnswers.withMatchingSuccess(newTrustUserAnswers)
          val registration = Await.result(registrationMapper.build(userAnswers, correspondenceAddress, trustName, Organisation), Duration.Inf).value

          val payload = Json.stringify(Json.toJson(registration))

          wiremock(
            payload = payload,
            expectedStatus = Status.INTERNAL_SERVER_ERROR,
            expectedResponse =
              """
                |{
                | "code": "INTERNAL_SERVER_ERROR",
                |  "message": "Internal server error."
                |}
                             """.stripMargin,
            url = registerUrl
          )

          val result = Await.result(connector.register(Json.toJson(registration), TestUserAnswers.draftId), Duration.Inf)
          result mustBe TrustResponse.InternalServerError
        }
      }

      "return InternalServerError response " - {

        "api returns bad request response " in {
          val userAnswers = TestUserAnswers.withMatchingSuccess(newTrustUserAnswers)
          val registration = Await.result(registrationMapper.build(userAnswers, correspondenceAddress, trustName, Organisation), Duration.Inf).value

          val payload = Json.stringify(Json.toJson(registration))

          wiremock(
            payload = payload,
            expectedStatus = Status.BAD_REQUEST,
            expectedResponse = "",
            url = registerUrl
          )

          val result = Await.result(connector.register(Json.toJson(registration), TestUserAnswers.draftId), Duration.Inf)
          result mustBe TrustResponse.InternalServerError
        }
      }

      "return InternalServerError response " - {

        "api returns service unavailable response " in {
          val userAnswers = TestUserAnswers.withMatchingSuccess(newTrustUserAnswers)
          val registration = Await.result(registrationMapper.build(userAnswers, correspondenceAddress, trustName, Organisation), Duration.Inf).value

          val payload = Json.stringify(Json.toJson(registration))

          wiremock(
            payload = payload,
            expectedStatus = Status.SERVICE_UNAVAILABLE,
            expectedResponse = "",
            url = registerUrl
          )

          val result = Await.result(connector.register(Json.toJson(registration), TestUserAnswers.draftId), Duration.Inf)
          result mustBe TrustResponse.InternalServerError
        }
      }
    }

    ".matching" - {

      val matchingUrl: String = "/trusts/check"

      val matchData: MatchData = MatchData("utr", "name", Some("postcode"))
      val payload: String = Json.stringify(Json.toJson(matchData)(MatchData.writes))

      "must return a Matched response" - {
        "when trust is successfully matched" in {

          wiremock(
            payload = payload,
            expectedStatus = Status.OK,
            expectedResponse = """{"match": true}""".stripMargin,
            url = matchingUrl
          )

          val result = Await.result(connector.matching(matchData), Duration.Inf)

          result mustBe SuccessOrFailureResponse(true)
        }
      }

      "must return a NotMatched response" - {
        "when trust fails matching" in {

          wiremock(
            payload = payload,
            expectedStatus = Status.OK,
            expectedResponse = """{"match": false}""".stripMargin,
            url = matchingUrl
          )

          val result = Await.result(connector.matching(matchData), Duration.Inf)

          result mustBe SuccessOrFailureResponse(false)
        }
      }

      "return AlreadyRegistered response " - {
        "when trust has already been registered" in {

          wiremock(
            payload = payload,
            expectedStatus = Status.CONFLICT,
            expectedResponse =
              """
                |{
                | "code": "ALREADY_REGISTERED",
                | "message": "The trust is already registered."
                |}
                             """.stripMargin,
            url = matchingUrl
          )

          val result = Await.result(connector.matching(matchData), Duration.Inf)
          result mustBe MatchedResponse.AlreadyRegistered
        }
      }

      "return InternalServerError response " - {
        "when there is an internal server error" in {

          wiremock(
            payload = payload,
            expectedStatus = Status.INTERNAL_SERVER_ERROR,
            expectedResponse =
              """
                |{
                | "code": "INTERNAL_SERVER_ERROR",
                | "message": "Internal server error."
                |}
                             """.stripMargin,
            url = matchingUrl
          )

          val result = Await.result(connector.matching(matchData), Duration.Inf)
          result mustBe MatchedResponse.InternalServerError
        }
      }
    }
  }
}
