/*
 * Copyright 2026 HM Revenue & Customs
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

import models.core.pages.{Declaration, FullName}
import models.core.{MatchingAndSuitabilityUserAnswers, UserAnswers}
import models.registration.Matched.{Failed, Success}
import org.scalatest.TryValues
import pages.register._

object TestUserAnswers extends TryValues {

  lazy val draftId        = "id"
  lazy val userInternalId = "internalId"

  def emptyUserAnswers: UserAnswers = UserAnswers(draftId = draftId, internalAuthId = userInternalId)

  def emptyMatchingAndSuitabilityUserAnswers: MatchingAndSuitabilityUserAnswers =
    MatchingAndSuitabilityUserAnswers(internalId = userInternalId)

  def withDeclaration(userAnswers: UserAnswers): UserAnswers =
    userAnswers
      .set(DeclarationPage, Declaration(FullName("First", None, "Last"), Some("test@test.comn")))
      .success
      .value

  def withMatchingSuccess(userAnswers: UserAnswers): UserAnswers =
    userAnswers
      .set(TrustHaveAUTRPage, true)
      .success
      .value
      .set(WhatIsTheUTRPage, "123456789")
      .success
      .value
      .set(PostcodeForTheTrustPage, "NE981ZZ")
      .success
      .value
      .set(ExistingTrustMatched, Success)
      .success
      .value

  def withMatchingFailed(userAnswers: UserAnswers): UserAnswers =
    userAnswers
      .set(ExistingTrustMatched, Failed)
      .success
      .value

}
