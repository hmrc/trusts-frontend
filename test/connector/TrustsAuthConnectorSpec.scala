/*
 * Copyright 2021 HM Revenue & Customs
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

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import models.{TrustsAuthAllowed, TrustsAuthDenied, TrustsAuthInternalServerError}
import org.scalatest.{AsyncFreeSpec, MustMatchers}
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.test.DefaultAwaitTimeout
import uk.gov.hmrc.http.HeaderCarrier
import utils.WireMockHelper

class TrustsAuthConnectorSpec extends AsyncFreeSpec with MustMatchers with WireMockHelper with DefaultAwaitTimeout{

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private def responseFromJson(json: JsValue): ResponseDefinitionBuilder = {
    aResponse().withStatus(Status.OK).withBody(json.toString())
  }

  private def wiremock(url: String, response: ResponseDefinitionBuilder): StubMapping = {
    server
      .stubFor(
        post(urlEqualTo(url))
          .willReturn(response)
      )
  }

  lazy val app: Application = new GuiceApplicationBuilder()
    .configure(Seq(
      "microservice.services.trusts-auth.port" -> server.port(),
      "auditing.enabled" -> false
    ): _*).build()

  private lazy val connector = app.injector.instanceOf[TrustsAuthConnector]

  "TrustAuthConnector" - {

    "authoriseAccessCode" - {

      val draftId = "draftId"
      val url = s"/trusts-auth/$draftId/access-code-for-non-taxable"
      val accessCode = "accessCode"

      "must return TrustsAuthAllowed" - {
        "when service returns OK with authorised body" in {

          val response = responseFromJson(Json.obj("authorised" -> true))

          wiremock(url, response)

          connector.authoriseAccessCode(draftId, accessCode) map { result =>
            result mustEqual TrustsAuthAllowed()
          }
        }
      }

      "must return TrustsAuthDenied" - {
        "when service returns OK with redirectUrl body" in {

          val redirectUrl = "redirect-url"
          val response = responseFromJson(Json.obj("redirectUrl" -> redirectUrl))

          wiremock(url, response)

          connector.authoriseAccessCode(draftId, accessCode) map { result =>
            result mustEqual TrustsAuthDenied(redirectUrl)
          }
        }
      }

      "must return TrustsAuthInternalServerError" - {
        "when service returns INTERNAL_SERVER_ERROR" in {

          val response = aResponse().withStatus(Status.INTERNAL_SERVER_ERROR)

          wiremock(url, response)

          connector.authoriseAccessCode(draftId, accessCode) map { result =>
            result mustEqual TrustsAuthInternalServerError
          }
        }
      }
    }
  }
}
