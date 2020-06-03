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

package pages.register.trust_details

import models.registration.pages.NonResidentType.Domiciled
import models.registration.pages.Status.InProgress
import pages.behaviours.PageBehaviours
import pages.entitystatus.TrustDetailsStatus

class SettlorsBasedInTheUKPageSpec extends PageBehaviours {

  "SettlorsBasedInTheUKPage" must {

    beRetrievable[Boolean](SettlorsBasedInTheUKPage)

    beSettable[Boolean](SettlorsBasedInTheUKPage)

    beRemovable[Boolean](SettlorsBasedInTheUKPage)

    "implement cleanup logic when YES selected" in {
      val userAnswers = emptyAnswers
        .set(RegisteringTrustFor5APage, false)
        .flatMap(_.set(InheritanceTaxActPage, false))
        .flatMap(_.set(NonResidentTypePage, Domiciled))
        .flatMap(_.set(AgentOtherThanBarristerPage, true))
        .flatMap(_.set(TrustDetailsStatus, InProgress))
        .flatMap(_.set(SettlorsBasedInTheUKPage, true))

      userAnswers.get.get(RegisteringTrustFor5APage) mustNot be(defined)
      userAnswers.get.get(InheritanceTaxActPage) mustNot be(defined)
      userAnswers.get.get(NonResidentTypePage) mustNot be(defined)
      userAnswers.get.get(AgentOtherThanBarristerPage) mustNot be(defined)
      userAnswers.get.get(TrustDetailsStatus) mustNot be(defined)
    }

    "implement cleanup logic when NO selected" in {
      val userAnswers = emptyAnswers
        .set(EstablishedUnderScotsLawPage, false)
        .flatMap(_.set(TrustResidentOffshorePage, false))
        .flatMap(_.set(TrustPreviouslyResidentPage, "country"))
        .flatMap(_.set(TrustDetailsStatus, InProgress))
        .flatMap(_.set(SettlorsBasedInTheUKPage, false))

      userAnswers.get.get(EstablishedUnderScotsLawPage) mustNot be(defined)
      userAnswers.get.get(TrustResidentOffshorePage) mustNot be(defined)
      userAnswers.get.get(TrustPreviouslyResidentPage) mustNot be(defined)
      userAnswers.get.get(TrustDetailsStatus) mustNot be(defined)
    }
  }
}
