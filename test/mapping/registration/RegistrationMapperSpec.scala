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

package mapping.registration

import base.RegistrationSpecBase
import models.core.http.{AddressType, Correspondence, Declaration, MatchData, Registration}
import models.core.pages.FullName
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import play.api.libs.json.Json
import uk.gov.hmrc.auth.core.AffinityGroup._
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUserAnswers

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class RegistrationMapperSpec extends RegistrationSpecBase {

  private val name = FullName("Joe", None, "Blogss")
  private val trustName = "Trust Name"
  private val postcode = "AB1 1AB"
  private val correspondenceAddress = AddressType("line1", "line2", None, None, Some(postcode), "GB")
  private val utr = "1234567890"

  implicit val hc: HeaderCarrier = HeaderCarrier()

  "RegistrationMapper" when {

    "user answers is empty" must {

      val registrationMapper: RegistrationMapper = injector.instanceOf[RegistrationMapper]

      "not be able to create Registration" in {

        val userAnswers = TestUserAnswers.emptyUserAnswers

        Await.result(registrationMapper.build(userAnswers, correspondenceAddress, trustName, Organisation), Duration.Inf) mustNot be(defined)
      }
    }

    "user answers is not empty" when {

      val declarationMapper = mock[DeclarationMapper]
      val correspondenceMapper = mock[CorrespondenceMapper]
      val matchingMapper = mock[MatchingMapper]

      val registrationMapper = new RegistrationMapper(declarationMapper, correspondenceMapper, matchingMapper)

      val declaration = Declaration(name, correspondenceAddress)
      val matchData = MatchData(utr, trustName, Some(postcode))
      val correspondence = Correspondence(trustName)

      "agent" must {
        "create registration with some empty agent details" in {

          when(declarationMapper.build(any(), any())(any(), any())).thenReturn(Future.successful(Some(declaration)))
          when(correspondenceMapper.build(any())).thenReturn(correspondence)
          when(matchingMapper.build(any(), any())).thenReturn(Some(matchData))

          val result = Await.result(registrationMapper.build(emptyUserAnswers, correspondenceAddress, trustName, Agent), Duration.Inf).get

          result mustBe Registration(
            matchData = Some(matchData),
            correspondence = correspondence,
            declaration = declaration,
            trust = Json.obj(),
            agentDetails = Some(Json.obj())
          )
        }
      }

      "organisation" must {
        "create registration with no agent details" in {

          when(declarationMapper.build(any(), any())(any(), any())).thenReturn(Future.successful(Some(declaration)))
          when(correspondenceMapper.build(any())).thenReturn(correspondence)
          when(matchingMapper.build(any(), any())).thenReturn(Some(matchData))

          val result = Await.result(registrationMapper.build(emptyUserAnswers, correspondenceAddress, trustName, Organisation), Duration.Inf).get

          result mustBe Registration(
            matchData = Some(matchData),
            correspondence = correspondence,
            declaration = declaration,
            trust = Json.obj(),
            agentDetails = None
          )
        }
      }
    }
  }
}
