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

import models.requests.MatchingAndSuitabilityDataRequest
import models.{FirstTaxYearAvailable, TaskStatuses}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.AnyContent
import repositories.{CacheRepository, RegistrationsRepository}
import services.{DraftRegistrationService, SubmissionService, TrustsStoreService}
import utils.TestUserAnswers
import viewmodels.RegistrationAnswerSections

import scala.concurrent.Future

trait Mocked extends MockitoSugar {

  val cacheRepository: CacheRepository = mock[CacheRepository]
  val registrationsRepository: RegistrationsRepository = mock[RegistrationsRepository]
  val mockSubmissionService: SubmissionService = mock[SubmissionService]
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
}
