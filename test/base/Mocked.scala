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

package base

import models.requests.MatchingAndSuitabilityDataRequest
import models.{FirstTaxYearAvailable, RegistrationSubmission, TaskStatuses}
import org.mockito.ArgumentMatchers.any
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.when
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.AnyContent
import play.api.test.Helpers.OK
import repositories.{CacheRepository, RegistrationsRepository}
import services.{DraftRegistrationService, SubmissionService, TrustsStoreService}
import uk.gov.hmrc.http.HttpResponse
import utils.TestUserAnswers
import viewmodels.RegistrationAnswerSections

import scala.concurrent.Future

trait Mocked extends MockitoSugar {

  val cacheRepository: CacheRepository                 = mock[CacheRepository]
  val registrationsRepository: RegistrationsRepository = mock[RegistrationsRepository]
  val mockSubmissionService: SubmissionService         = mock[SubmissionService]

  val mockCreateDraftRegistrationService: DraftRegistrationService =
    mock[DraftRegistrationService]

  val mockTrustsStoreService: TrustsStoreService = mock[TrustsStoreService]

  when(mockCreateDraftRegistrationService.create(any[MatchingAndSuitabilityDataRequest[AnyContent]])(any()))
    .thenReturn(Future.successful(TestUserAnswers.draftId))

  when(mockCreateDraftRegistrationService.getAnswerSections(any())(any(), any()))
    .thenReturn(Future.successful(RegistrationAnswerSections()))

  when(cacheRepository.set(any())).thenReturn(Future.successful(true))

  when(registrationsRepository.set(any(), any())(any())).thenReturn(Future.successful(true))

  when(registrationsRepository.getFirstTaxYearAvailable(any())(any()))
    .thenReturn(Future.successful(Some(FirstTaxYearAvailable(2, earlierYearsToDeclare = false))))

  when(mockTrustsStoreService.getTaskStatuses(any())(any(), any()))
    .thenReturn(Future.successful(TaskStatuses.withAllComplete))

  when(registrationsRepository.getDraftSettlors(any())(any()))
    .thenReturn(Future.successful(Json.parse("{}")))

  when(registrationsRepository.getSettlorsAnswerSections(any())(any()))
    .thenReturn(Future.successful(Seq.empty[RegistrationSubmission.AnswerSection]))

  when(registrationsRepository.getRegistrationPieces(any())(any()))
    .thenReturn(Future.successful(Json.parse("{}").as[JsObject]))

  when(registrationsRepository.setDraftSettlors(any(), any())(any()))
    .thenReturn(Future.successful(HttpResponse(OK, "")))

}
