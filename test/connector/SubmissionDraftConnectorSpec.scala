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

package connector

import java.time.{LocalDate, LocalDateTime}

import base.SpecBaseHelpers
import com.github.tomakehurst.wiremock.client.WireMock._
import models.RegistrationSubmission.{AllAnswerSections, AllStatus, AnswerRow, AnswerSection}
import models.core.http.{AddressType, IdentificationOrgType, LeadTrusteeOrgType, LeadTrusteeType}
import models.registration.pages.Status.{Completed, InProgress}
import models.{RegistrationSubmission, SubmissionDraftData, SubmissionDraftId, SubmissionDraftResponse}
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.CONTENT_TYPE
import uk.gov.hmrc.http.HeaderCarrier
import utils.WireMockHelper

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class SubmissionDraftConnectorSpec extends FreeSpec with MustMatchers with OptionValues with SpecBaseHelpers with WireMockHelper {
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(Seq(
      "microservice.services.trusts.port" -> server.port(),
      "auditing.enabled" -> false): _*
    ).build()

  private lazy val connector = injector.instanceOf[SubmissionDraftConnector]

  private val testDraftId = "draftId"
  private val submissionsUrl = s"/trusts/register/submission-drafts"
  private val mainUrl = s"$submissionsUrl/$testDraftId/main"
  private val resetTaxLiabilityUrl = s"$submissionsUrl/$testDraftId/reset/taxLiability"
  private val beneficiariesUrl = s"$submissionsUrl/$testDraftId/beneficiaries"
  private val statusUrl = s"$submissionsUrl/$testDraftId/status"
  private val registrationUrl = s"$submissionsUrl/$testDraftId/registration"
  private val answerSectionsUrl = s"$submissionsUrl/$testDraftId/answerSections"
  private val leadTrusteeUrl = s"$submissionsUrl/$testDraftId/lead-trustee"
  private val correspondenceAddressUrl = s"$submissionsUrl/$testDraftId/correspondence-address"
  private val agentAddressUrl = s"$submissionsUrl/$testDraftId/agent-address"
  private val clientReferenceUrl = s"$submissionsUrl/$testDraftId/client-reference"
  private val trustSetupDateUrl = s"$submissionsUrl/$testDraftId/when-trust-setup"
  private val trustNameUrl = s"$submissionsUrl/$testDraftId/trust-name"

  "SubmissionDraftConnector" - {

    "submission drafts" - {

      "can be set for main" in {

        val sectionData = Json.parse(
          """
            |{
            | "field1": "value1",
            | "field2": "value2"
            |}
            |""".stripMargin)

        val submissionDraftData = SubmissionDraftData(sectionData, Some("ref"), Some(true))

        server.stubFor(
          post(urlEqualTo(mainUrl))
            .withHeader(CONTENT_TYPE, containing("application/json"))
            .withRequestBody(equalTo(Json.toJson(submissionDraftData).toString()))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
            )
        )

        val result = Await.result(connector.setDraftMain(testDraftId, sectionData, inProgress = true, Some("ref")), Duration.Inf)
        result.status mustBe Status.OK
      }

      "can be retrieved for main" in {

        val draftData = Json.parse(
          """
            |{
            | "field1": "value1",
            | "field2": "value2"
            |}
            |""".stripMargin)

        val draftResponseJson =
          """
            |{
            | "createdAt": "2012-02-03T09:30:00",
            | "data": {
            |  "field1": "value1",
            |  "field2": "value2"
            | }
            |}
            |""".stripMargin

        server.stubFor(
          get(urlEqualTo(mainUrl))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
                .withBody(draftResponseJson)
            )
        )

        val result: SubmissionDraftResponse = Await.result(connector.getDraftMain(testDraftId), Duration.Inf)
        result.createdAt mustBe LocalDateTime.of(2012, 2, 3, 9, 30)
        result.data mustBe draftData
      }

      "can be retrieved for beneficiaries" in {

        val draftData = Json.parse(
          """
            |{
            | "field1": "value1",
            | "field2": "value2"
            |}
            |""".stripMargin)

        val draftResponseJson =
          """
            |{
            | "createdAt": "2012-02-03T09:30:00",
            | "data": {
            |  "field1": "value1",
            |  "field2": "value2"
            | }
            |}
            |""".stripMargin

        server.stubFor(
          get(urlEqualTo(beneficiariesUrl))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
                .withBody(draftResponseJson)
            )
        )

        val result: SubmissionDraftResponse = Await.result(connector.getDraftBeneficiaries(testDraftId), Duration.Inf)
        result.createdAt mustBe LocalDateTime.of(2012, 2, 3, 9, 30)
        result.data mustBe draftData
      }
      "can have list of ids retrieved" in {

        val draftIdsResponseJson =
          """
            |[
            | {
            |   "draftId": "Draft1",
            |   "createdAt": "2012-02-03T09:30:00",
            |   "reference": "ref",
            |   "inProgress": true
            | },
            | {
            |   "draftId": "Draft2",
            |   "createdAt": "2010-06-21T14:44:00"
            | }
            |]
            |""".stripMargin

        server.stubFor(
          get(urlEqualTo(submissionsUrl))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
                .withBody(draftIdsResponseJson)
            )
        )

        val result = Await.result(connector.getCurrentDraftIds(), Duration.Inf)
        result mustBe List(
          SubmissionDraftId("Draft1", LocalDateTime.of(2012, 2, 3, 9, 30), Some("ref")),
          SubmissionDraftId("Draft2", LocalDateTime.of(2010, 6, 21, 14, 44), None)
        )
      }

      "can retrieve status for a draft" in {

        val allStatus = AllStatus(beneficiaries = Some(Completed))
        val response = SubmissionDraftResponse(LocalDateTime.now(), Json.toJson(allStatus), None)

        server.stubFor(
          get(urlEqualTo(statusUrl))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
                .withBody(Json.toJson(response).toString)
            )
        )

        val result = Await.result(connector.getStatus(testDraftId), Duration.Inf)
        result mustEqual allStatus
      }

      "can set status for a draft" in {

        val status = AllStatus(beneficiaries = Some(Completed), trustees = Some(InProgress))

        val submissionDraftData = SubmissionDraftData(Json.toJson(status), None, None)

        server.stubFor(
          post(urlEqualTo(statusUrl))
            .withHeader(CONTENT_TYPE, containing("application/json"))
            .withRequestBody(equalTo(Json.toJson(submissionDraftData).toString()))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
            )
        )

        val result = Await.result(connector.setStatus(testDraftId, status), Duration.Inf)
        result.status mustBe Status.OK
      }

      "can retrieve registrations for a draft" in {

        val resultJson = Json.obj(
          "path1" -> "value1",
          "path2" -> "value2"
        )

        val response = SubmissionDraftResponse(LocalDateTime.now(), resultJson, None)


        server.stubFor(
          get(urlEqualTo(registrationUrl))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
                .withBody(Json.toJson(response).toString)
            )
        )

        val result = Await.result(connector.getRegistrationPieces(testDraftId), Duration.Inf)
        result mustEqual resultJson
      }

      "can retrieve answer sections for a draft" in {

        val allAnswerSections = AllAnswerSections(
          beneficiaries = Some(
            List(
              AnswerSection(
                Some("headingKey1"),
                List(
                  AnswerRow("label1", "answer1", "labelArg1")
                ),
                Some("sectionKey1")),
              AnswerSection(
                Some("headingKey2"),
                List(
                  AnswerRow("label2", "answer2", "labelArg2")
                ),
                Some("sectionKey2"))
            )
          ),
          trustees = Some(
            List(
              RegistrationSubmission.AnswerSection(
                Some("trusteeHeadingKey1"),
                List(
                  RegistrationSubmission.AnswerRow("label1", "answer1", "labelArg1")
                ),
                Some("trusteeSectionKey1")),
              RegistrationSubmission.AnswerSection(
                Some("trusteeHeadingKey2"),
                List(
                  RegistrationSubmission.AnswerRow("label2", "answer2", "labelArg2")
                ),
                Some("trusteeSectionKey2"))
            )
          ),
          protectors = None,
          otherIndividuals = None,
          trustDetails = None,
          settlors = None,
          assets = None
        )

        val response = SubmissionDraftResponse(LocalDateTime.now(), Json.toJson(allAnswerSections), None)

        server.stubFor(
          get(urlEqualTo(answerSectionsUrl))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
                .withBody(Json.toJson(response).toString)
            )
        )

        val result = Await.result(connector.getAnswerSections(testDraftId), Duration.Inf)
        result mustEqual allAnswerSections
      }

      "can retrieve lead trustee for a draft" in {

        val response = Json.parse(
          """
            |{
            |  "leadTrusteeOrg": {
            |    "name": "Lead Org",
            |    "phoneNumber": "07911234567",
            |    "identification": {
            |      "address": {
            |        "line1": "line1",
            |        "line2": "line2",
            |        "postCode": "AA1 1AA",
            |        "country": "GB"
            |      }
            |    }
            |  }
            |}
            |""".stripMargin)

        server.stubFor(
          get(urlEqualTo(leadTrusteeUrl))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
                .withBody(Json.toJson(response).toString)
            )
        )

        val expectedLeadTrustee = LeadTrusteeType(
          None,
          Some(LeadTrusteeOrgType(
            "Lead Org",
            "07911234567",
            None,
            IdentificationOrgType(None, Some(AddressType("line1", "line2", None, None, Some("AA1 1AA"), "GB")))))
        )

        val result = Await.result(connector.getLeadTrustee(testDraftId), Duration.Inf)
        result mustEqual expectedLeadTrustee
      }

      "can retrieve correspondence address for a draft" in {

        val response = Json.parse(
          """
            |{
            | "line1": "Address line1",
            | "line2": "Address line2",
            | "postCode": "NE1 1EN",
            | "country": "GB"
            |}
            |""".stripMargin)

        server.stubFor(
          get(urlEqualTo(correspondenceAddressUrl))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
                .withBody(Json.toJson(response).toString)
            )
        )

        val expectedAddress = AddressType("Address line1", "Address line2", None, None, Some("NE1 1EN"), "GB")

        val result = Await.result(connector.getCorrespondenceAddress(testDraftId), Duration.Inf)
        result mustEqual expectedAddress
      }

      "can retrieve agent address for a draft" in {

        val response = Json.parse(
          """
            |{
            | "line1": "Address line1",
            | "line2": "Address line2",
            | "postCode": "NE1 1EN",
            | "country": "GB"
            |}
            |""".stripMargin)

        server.stubFor(
          get(urlEqualTo(agentAddressUrl))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
                .withBody(Json.toJson(response).toString)
            )
        )

        val expectedAddress = AddressType("Address line1", "Address line2", None, None, Some("NE1 1EN"), "GB")

        val result = Await.result(connector.getAgentAddress(testDraftId), Duration.Inf)
        result mustEqual expectedAddress
      }

      "can retrieve client reference for a draft" in {

        val response = Json.parse(
          """
            |"client-ref"
            |""".stripMargin)

        server.stubFor(
          get(urlEqualTo(clientReferenceUrl))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
                .withBody(Json.toJson(response).toString)
            )
        )

        val expected = "client-ref"

        val result = Await.result(connector.getClientReference(testDraftId), Duration.Inf)
        result mustEqual expected
      }

      "can retrieve trust setup date for a draft" in {

        val response = Json.obj(
          "startDate" -> "2012-02-20"
        )

        server.stubFor(
          get(urlEqualTo(trustSetupDateUrl))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
                .withBody(response.toString)
            )
        )

        val result = Await.result(connector.getTrustSetupDate(testDraftId), Duration.Inf)
        result mustEqual Some(LocalDate.of(2012, 2, 20))
      }

      "can handle draft without trust setup date" in {

        server.stubFor(
          get(urlEqualTo(trustSetupDateUrl))
            .willReturn(
              aResponse()
                .withStatus(Status.NOT_FOUND)
            )
        )

        val result = Await.result(connector.getTrustSetupDate(testDraftId), Duration.Inf)
        result mustEqual None
      }

      "can retrieve trust name for a draft" in {

        val response = Json.obj(
          "trustName" -> "My Lovely Trust"
        )

        server.stubFor(
          get(urlEqualTo(trustNameUrl))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
                .withBody(response.toString)
            )
        )

        val result = Await.result(connector.getTrustName(testDraftId), Duration.Inf)
        result mustEqual "My Lovely Trust"
      }

      ".resetTaxLiability" - {

        "resets tax liability" in {

          server.stubFor(
            post(urlEqualTo(resetTaxLiabilityUrl))
              .willReturn(
                aResponse()
                  .withStatus(Status.OK)
              )
          )

          val result = Await.result(connector.resetTaxLiability(testDraftId), Duration.Inf)
          result.status mustBe Status.OK
        }

      }

      ".removeDraft" in {

        val draftId: String = "draftId"

        server.stubFor(
          delete(urlEqualTo(s"$submissionsUrl/$draftId"))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
            )
        )

        val result = Await.result(connector.removeDraft(draftId), Duration.Inf)
        result.status mustBe Status.OK
      }
    }
  }
}
