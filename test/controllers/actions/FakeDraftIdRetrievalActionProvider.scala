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

package controllers.actions

import controllers.actions.register.{DraftIdDataRetrievalAction, DraftIdRetrievalActionProvider}
import models.core.{TrustsFrontendUserAnswers, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.when
import repositories.RegistrationsRepository

import scala.concurrent.{ExecutionContext, Future}

class FakeDraftIdRetrievalActionProvider(dataToReturn: Option[TrustsFrontendUserAnswers[_]]) extends DraftIdRetrievalActionProvider with MockitoSugar {

  implicit val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  val mockedRegistrationsRepository: RegistrationsRepository = mock[RegistrationsRepository]

  dataToReturn match {
    case Some(x: UserAnswers) =>
      when(mockedRegistrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(x)))
    case _ =>
      when(mockedRegistrationsRepository.get(any())(any())).thenReturn(Future.successful(None))
  }

  override def apply(draftId: String) = new DraftIdDataRetrievalAction(draftId, mockedRegistrationsRepository, executionContext)

}
