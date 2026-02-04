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

package services

import base.RegistrationSpecBase
import models.core.UserAnswers
import models.requests.MatchingAndSuitabilityDataRequest
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify
import org.scalatest.concurrent.ScalaFutures
import pages.register.suitability.{ExpressTrustYesNoPage, TaxLiabilityInCurrentTaxYearYesNoPage}
import pages.register.{TrustHaveAUTRPage, TrustRegisteredOnlinePage}
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class DraftRegistrationServiceSpec extends RegistrationSpecBase with ScalaFutures {

  val service: DraftRegistrationService = new DraftRegistrationService(registrationsRepository)

  "DraftRegistrationService" when {

    ".create" must {
      "take matching and suitability answers and set them in the registrations repository" in {

        implicit val hc: HeaderCarrier = HeaderCarrier()

        val userAnswers = emptyMatchingAndSuitabilityUserAnswers
          .set(TrustRegisteredOnlinePage, false)
          .success
          .value
          .set(TrustHaveAUTRPage, false)
          .success
          .value
          .set(ExpressTrustYesNoPage, true)
          .success
          .value
          .set(TaxLiabilityInCurrentTaxYearYesNoPage, true)
          .success
          .value

        val request = MatchingAndSuitabilityDataRequest(
          FakeRequest(),
          userAnswers.internalId,
          "sessionId",
          userAnswers,
          Organisation,
          Enrolments(Set())
        )

        val draftId = Await.result(service.create(request), Duration.Inf)

        val uaCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(registrationsRepository).set(uaCaptor.capture, any())(any())

        uaCaptor.getValue.draftId mustEqual draftId
        uaCaptor.getValue.data mustEqual request.userAnswers.data
        uaCaptor.getValue.internalAuthId mustEqual request.internalId
      }
    }
  }

}
