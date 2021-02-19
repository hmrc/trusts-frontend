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

package base

import controllers.Assets.OK

import java.time.LocalDate
import models.RegistrationSubmission.AllStatus
import models.requests.{IdentifierRequest, OptionalRegistrationDataRequest}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.AnyContent
import repositories.RegistrationsRepository
import services.{DraftRegistrationService, SubmissionService}
import uk.gov.hmrc.http.HttpResponse
import utils.TestUserAnswers
import viewmodels.RegistrationAnswerSections

import scala.concurrent.Future

trait Mocked extends MockitoSugar {

  val registrationsRepository : RegistrationsRepository = mock[RegistrationsRepository]

  val mockSubmissionService : SubmissionService = mock[SubmissionService]
  val mockCreateDraftRegistrationService : DraftRegistrationService = mock[DraftRegistrationService]

  when(mockCreateDraftRegistrationService.create(any[OptionalRegistrationDataRequest[AnyContent]])(any()))
    .thenReturn(Future.successful(TestUserAnswers.draftId))

  when(mockCreateDraftRegistrationService.create(any[IdentifierRequest[AnyContent]])(any()))
    .thenReturn(Future.successful(TestUserAnswers.draftId))

  when(mockCreateDraftRegistrationService.getAnswerSections(any())(any()))
    .thenReturn(Future.successful(RegistrationAnswerSections()))

  when(registrationsRepository.set(any())(any())).thenReturn(Future.successful(true))
  when(registrationsRepository.getAllStatus(any())(any())).thenReturn(Future.successful(AllStatus()))
  when(registrationsRepository.updateTaxLiability(any())(any())).thenReturn(Future.successful(HttpResponse(OK, "")))

  val mockedTrustStartDate: LocalDate = LocalDate.parse("2019-02-03")
  when(registrationsRepository.getTrustSetupDate(any())(any())).thenReturn(Future.successful(Some(mockedTrustStartDate)))
}
