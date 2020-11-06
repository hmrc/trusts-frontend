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

package utils

import models.core.UserAnswers
import models.core.pages.{Declaration, FullName, UKAddress}
import models.registration.Matched.{Failed, Success}
import models.registration.pages.Status.Completed
import models.registration.pages._
import org.scalatest.TryValues
import pages.entitystatus._
import pages.register._
import pages.register.agents._
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.{AddAssetsPage, WhatKindOfAssetPage}
import play.api.libs.json.Json

object TestUserAnswers extends TryValues {

  lazy val draftId = "id"
  lazy val userInternalId = "internalId"

  def emptyUserAnswers: UserAnswers = models.core.UserAnswers(draftId, Json.obj(), internalAuthId = userInternalId)

  def withAgent(userAnswers: UserAnswers): UserAnswers = {
    userAnswers
      .set(AgentARNPage, "SARN1234567").success.value
      .set(AgentNamePage, "Agency Name").success.value
      .set(AgentUKAddressPage, UKAddress("line1", "line2", Some("line3"), Some("line4"), "ab1 1ab")).success.value
      .set(AgentTelephoneNumberPage, "+1234567890").success.value
      .set(AgentInternalReferencePage, "1234-5678").success.value
      .set(AgentAddressYesNoPage, true).success.value
  }

  def withMoneyAsset(userAnswers: UserAnswers): UserAnswers = {
    val index = 0
    userAnswers
      .set(WhatKindOfAssetPage(index), WhatKindOfAsset.Money).success.value
      .set(AssetMoneyValuePage(index), "2000").success.value
      .set(AssetStatus(index), Completed).success.value
  }

  def withDeclaration(userAnswers: UserAnswers): UserAnswers = {
    userAnswers
      .set(DeclarationPage, Declaration(FullName("First", None, "Last"), Some("test@test.comn"))).success.value
  }

  def withMatchingSuccess(userAnswers: UserAnswers): UserAnswers = {
    userAnswers
      .set(TrustHaveAUTRPage, true).success.value
      .set(WhatIsTheUTRPage, "123456789").success.value
      .set(PostcodeForTheTrustPage, "NE981ZZ").success.value
      .set(ExistingTrustMatched, Success).success.value
  }

  def withMatchingFailed(userAnswers: UserAnswers): UserAnswers = {
    userAnswers
      .set(ExistingTrustMatched, Failed).success.value
  }

  def withCompleteSections(userAnswers: UserAnswers): UserAnswers = {
    userAnswers
      .set(AddAssetsPage, AddAssets.NoComplete).success.value
  }

  def newTrustCompleteUserAnswers: UserAnswers = {
    val emptyUserAnswers = TestUserAnswers.emptyUserAnswers
    val userAnswers = TestUserAnswers.withMoneyAsset(emptyUserAnswers)

    userAnswers
  }
}
