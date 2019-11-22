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
import com.github.tomakehurst.wiremock.client.WireMock._
import generators.Generators
import mapping.{Mapping, Registration, RegistrationMapper}
import models.TrustResponse._
import models.core.http.RegistrationTRNResponse
import models.playback.{Processed, Processing, TrustServiceUnavailable, UtrNotFound}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpec, Inside, MustMatchers, OptionValues}
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.CONTENT_TYPE
import uk.gov.hmrc.http.HeaderCarrier
import utils.{TestUserAnswers, WireMockHelper}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class TrustConnectorSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers with WireMockHelper with ScalaFutures with Inside {
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(Seq(
      "microservice.services.trusts.port" -> server.port(),
      "auditing.enabled" -> false): _*
    ).build()

  private lazy val registrationMapper: Mapping[Registration] = injector.instanceOf[RegistrationMapper]

  private lazy val connector = injector.instanceOf[TrustConnector]

  private val registerUrl : String = "/trusts/register"

  private def playbackUrl(utr: String) : String = s"/trusts/$utr"

  private def wiremock(payload: String, expectedStatus: Int, expectedResponse : String)=
    server.stubFor(
      post(urlEqualTo(registerUrl))
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

        val result  = Await.result(connector.register(registration, TestUserAnswers.draftId),Duration.Inf)
        result mustBe RegistrationTRNResponse("XTRN1234567")
      }
    }

    "return AlreadyRegistered response " - {
      "already registered trusts is sent " in {
        val userAnswers = TestUserAnswers.withMatchingSuccess(newTrustUserAnswers)
        val registration = registrationMapper.build(userAnswers).value

        val payload = Json.stringify(Json.toJson(registration))

        wiremock(
          payload = payload,
          expectedStatus = Status.CONFLICT,
          expectedResponse = """
                               |{
                               | "code": "ALREADY_REGISTERED",
                               |  "message": "Trusts already registered."
                               |}
                             """.stripMargin
        )

        val result  = Await.result(connector.register(registration, TestUserAnswers.draftId),Duration.Inf)
        result mustBe AlreadyRegistered
      }
    }

    "return InternalServerError response " - {
      "api returns internal server error response " in {
        val userAnswers = TestUserAnswers.withMatchingSuccess(newTrustUserAnswers)
        val registration = registrationMapper.build(userAnswers).value

        val payload = Json.stringify(Json.toJson(registration))

        wiremock(
          payload = payload,
          expectedStatus = Status.INTERNAL_SERVER_ERROR,
          expectedResponse = """
                               |{
                               | "code": "INTERNAL_SERVER_ERROR",
                               |  "message": "Internal server error."
                               |}
                             """.stripMargin
        )

        val result  = Await.result(connector.register(registration, TestUserAnswers.draftId),Duration.Inf)
        result mustBe InternalServerError
      }
    }

    "return InternalServerError response " - {
      "api returns bad request response " in {
        val userAnswers = TestUserAnswers.withMatchingSuccess(newTrustUserAnswers)
        val registration = registrationMapper.build(userAnswers).value

        val payload = Json.stringify(Json.toJson(registration))

        wiremock(
          payload = payload,
          expectedStatus = Status.BAD_REQUEST,
          expectedResponse = ""
        )

        val result  = Await.result(connector.register(registration, TestUserAnswers.draftId),Duration.Inf)
        result mustBe InternalServerError
      }
    }

    "return InternalServerError response " - {
      "api returns service unavailable response " in {
        val userAnswers = TestUserAnswers.withMatchingSuccess(newTrustUserAnswers)
        val registration = registrationMapper.build(userAnswers).value

        val payload = Json.stringify(Json.toJson(registration))

        wiremock(
          payload = payload,
          expectedStatus = Status.SERVICE_UNAVAILABLE,
          expectedResponse = ""
        )

        val result  = Await.result(connector.register(registration, TestUserAnswers.draftId),Duration.Inf)
        result mustBe InternalServerError
      }
    }

    "return TrustFound response" in {

      val utr = "10000000008"

      server.stubFor(
        get(urlEqualTo(playbackUrl(utr)))
          .willReturn(
            aResponse()
              .withStatus(Status.OK)
              .withBody("""{
                          |
                          |  "responseHeader": {
                          |    "status": "In Processing",
                          |    "formBundleNo": "1"
                          |  }
                          |}""".stripMargin)
          )
      )

      val result  = Await.result(connector.playback(utr),Duration.Inf)
      result mustBe Processing
    }

    "return NotFound response" in {

      val utr = "10000000008"

      server.stubFor(
        get(urlEqualTo(playbackUrl(utr)))
          .willReturn(
            aResponse()
              .withStatus(Status.NOT_FOUND)))

      val result  = Await.result(connector.playback(utr),Duration.Inf)
      result mustBe UtrNotFound
    }

    "return ServiceUnavailable response" in {

      val utr = "10000000008"

      server.stubFor(
        get(urlEqualTo(playbackUrl(utr)))
          .willReturn(
            aResponse()
              .withStatus(Status.SERVICE_UNAVAILABLE)))

      val result  = Await.result(connector.playback(utr), Duration.Inf)
      result mustBe TrustServiceUnavailable
    }

    "must return playback data inside a Processed trust" in {

      val utr = "10000000008"
      val payload = """{
                      |  "responseHeader": {
                      |    "status": "Processed",
                      |    "formBundleNo": "1"
                      |  }
                      |}""".stripMargin
      server.stubFor(
        get(urlEqualTo(playbackUrl(utr)))
          .willReturn(
            aResponse()
              .withStatus(Status.OK)
              .withBody(payload)
          )
      )

      val processed = Await.result(connector.playback(utr), Duration.Inf)

      inside(processed) {
        case Processed(data) => data mustBe Json.parse(payload)
      }
    }
  }

}
