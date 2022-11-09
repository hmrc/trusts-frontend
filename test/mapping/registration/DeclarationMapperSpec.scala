/*
 * Copyright 2022 HM Revenue & Customs
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

import base.SpecBaseHelpers
import generators.Generators
import models.core.http.{AddressType, Declaration}
import models.core.pages.FullName
import org.mockito.ArgumentMatchers.any
import org.scalatest.matchers.must.Matchers
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.OptionValues
import pages.register.DeclarationPage
import repositories.DefaultRegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class DeclarationMapperSpec extends AnyFreeSpec with Matchers
  with OptionValues with Generators with SpecBaseHelpers {

  private implicit val hc: HeaderCarrier = HeaderCarrier()

  private val leadTrusteeAddress = AddressType("Line 1", "Line 2", None, None, None, "FR")
  private val agentAddress: AddressType = AddressType("Line 1", "Line 2", None, None, Some("AB1 1AB"), "GB")

  "DeclarationMapper" - {

    "when DeclarationPage empty" - {

      "must not be able to create Declaration" in {

        val userAnswers = emptyUserAnswers

        val mockRegistrationsRepository: DefaultRegistrationsRepository = mock[DefaultRegistrationsRepository]
        when(mockRegistrationsRepository.getAgentAddress(any())(any())).thenReturn(Future.successful(Some(agentAddress)))
        val declarationMapper: DeclarationMapper = new DeclarationMapper(mockRegistrationsRepository)

        val result = Await.result(declarationMapper.build(userAnswers, leadTrusteeAddress), Duration.Inf)

        result mustNot be(defined)
      }
    }

    "when DeclarationPage not empty" - {

      val userAnswers = emptyUserAnswers
        .set(DeclarationPage, models.core.pages.Declaration(FullName("First", None, "Last"), Some("test@test.comn"))).success.value

      "for an Agent" - {

        "must be able to create declaration" in {

          when(registrationsRepository.getAgentAddress(any())(any())).thenReturn(Future.successful(Some(agentAddress)))
          val declarationMapper: DeclarationMapper = new DeclarationMapper(registrationsRepository)

          val result = Await.result(declarationMapper.build(userAnswers, leadTrusteeAddress), Duration.Inf).value

          result mustBe Declaration(
            name = FullName("First", None, "Last"),
            address = agentAddress
          )
        }
      }

      "for an Organisation" - {

        "must be able to create declaration" in {

          when(registrationsRepository.getAgentAddress(any())(any())).thenReturn(Future.successful(None))
          val declarationMapper: DeclarationMapper = new DeclarationMapper(registrationsRepository)
          
          val result = Await.result(declarationMapper.build(userAnswers, leadTrusteeAddress), Duration.Inf).value

          result mustBe Declaration(
            name = FullName("First", None, "Last"),
            address = leadTrusteeAddress
          )
        }
      }
    }
  }
}
