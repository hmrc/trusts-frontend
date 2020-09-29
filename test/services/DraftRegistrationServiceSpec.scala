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

package services

import java.time.LocalDateTime

import connector.SubmissionDraftConnector
import models.RegistrationSubmission.AllStatus
import models.SubmissionDraftResponse
import models.registration.pages.Status.InProgress
import org.mockito.Mockito._
import org.mockito.Matchers.{eq => eqTo, _}
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsArray, Json}
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class DraftRegistrationServiceSpec extends WordSpec with MustMatchers with MockitoSugar with ScalaFutures {

  val draftId = "draftId"

  val registrationsRepository : RegistrationsRepository = mock[RegistrationsRepository]
  val submissionDraftConnector : SubmissionDraftConnector = mock[SubmissionDraftConnector]

  implicit val hc: HeaderCarrier = new HeaderCarrier()

  val answersWithRole = Json.arr(
    Json.obj(
      "name" -> "Beneficiary 1",
      "roleInCompany" -> "Director",
      "status" -> "Completed"
    )
  )

  val answersWithoutRole = Json.arr(
    Json.obj(
      "name" -> "Beneficiary 1",
      "roleInCompany" -> "Director",
      "status" -> "Completed"
    )
  )

  def answers(beneficiaries: JsArray) = Json.obj(
    "id" -> "id",
    "data" -> Json.obj(
      "beneficiaries" -> Json.obj(
        "individualBeneficiaries" -> beneficiaries
      )
    ),
    "progress" -> "InProgress",
    "createdAt" -> LocalDateTime.now(),
    "internalId" -> "internalId"
  )

  "DraftRegistrationService" must {

    "update beneficiaries status" when {

      "there are beneficiaries listed without RoleInCompanyPage" in {

        when(registrationsRepository.setAllStatus(eqTo(draftId), eqTo(AllStatus(beneficiaries = Some(InProgress))))(any()))
          .thenReturn(Future.successful(true))

        when(submissionDraftConnector.getDraftBeneficiaries(eqTo(draftId))(any(), any()))
          .thenReturn(Future.successful(SubmissionDraftResponse(
            LocalDateTime.now(),
            answers(answersWithoutRole),
            None
          )))

        val app = new GuiceApplicationBuilder()
          .overrides(bind[RegistrationsRepository].toInstance(registrationsRepository))
          .overrides(bind[SubmissionDraftConnector].toInstance(submissionDraftConnector))
          .build()

        val service = app.injector.instanceOf[DraftRegistrationService]

        whenReady(service.setBeneficiaryStatus(draftId)){ result =>

          result mustBe true

        }

      }

    }

    "check beneficiaries and return successfully without status update" when {

      "all beneficiaries contain RoleInCompanyPage" in {

        when(submissionDraftConnector.getDraftBeneficiaries(eqTo(draftId))(any(), any()))
          .thenReturn(Future.successful(SubmissionDraftResponse(
            LocalDateTime.now(),
            answers(answersWithRole),
            None
          )))

        val app = new GuiceApplicationBuilder()
          .overrides(bind[RegistrationsRepository].toInstance(registrationsRepository))
          .overrides(bind[SubmissionDraftConnector].toInstance(submissionDraftConnector))
          .build()

        val service = app.injector.instanceOf[DraftRegistrationService]

        whenReady(service.setBeneficiaryStatus(draftId)){ result =>

          result mustBe true

        }

      }

      "there are no beneficiaries" in {

        when(submissionDraftConnector.getDraftBeneficiaries(eqTo(draftId))(any(), any()))
          .thenReturn(Future.successful(SubmissionDraftResponse(
            LocalDateTime.now(),
            Json.obj(),
            None
          )))

        val app = new GuiceApplicationBuilder()
          .overrides(bind[RegistrationsRepository].toInstance(registrationsRepository))
          .overrides(bind[SubmissionDraftConnector].toInstance(submissionDraftConnector))
          .build()

        val service = app.injector.instanceOf[DraftRegistrationService]

        whenReady(service.setBeneficiaryStatus(draftId)){ result =>

          result mustBe true

        }

      }

    }

  }

}
