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

package controllers.actions

import models.core.UserAnswers
import models.registration.pages.RegistrationStatus
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import repositories.RegistrationsRepository

import scala.concurrent.{ExecutionContext, Future}

class FakeDraftIdRetrievalActionProvider(draftId: String,
                                         status :  RegistrationStatus = RegistrationStatus.InProgress,
                                         dataToReturn : Option[UserAnswers],
                                         registrationsRepository : RegistrationsRepository)
  extends DraftIdRetrievalActionProvider with MockitoSugar {

  implicit val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  val mockedRegistrationsRepository = mock[RegistrationsRepository]

  when(mockedRegistrationsRepository.get(any(), any())).thenReturn(Future.successful(dataToReturn))

  override def apply(draftId : String) = new DraftIdDataRetrievalAction(draftId, mockedRegistrationsRepository, executionContext)

}




