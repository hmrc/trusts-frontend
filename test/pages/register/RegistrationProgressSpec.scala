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

package pages.register

import base.RegistrationSpecBase
import models.RegistrationSubmission.AllStatus
import models.core.UserAnswers
import models.core.pages.IndividualOrBusiness.Individual
import models.registration.pages.AddAssets.NoComplete
import models.registration.pages.Status.{Completed, InProgress}
import models.registration.pages._
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.entitystatus._
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.shares._
import pages.register.asset.{AddAssetsPage, WhatKindOfAssetPage}
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class RegistrationProgressSpec extends RegistrationSpecBase {
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  "Assets section" must {

    "render no tag" when {

      "there are no assets in user answers" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers

        registrationProgress.assetsStatus(userAnswers) mustBe None
      }

      "assets list is empty" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val json = Json.parse(
          """
            |{
            |   "assets" : []
            |}
            |""".stripMargin)

        val userAnswers = UserAnswers(draftId = fakeDraftId, data = json.as[JsObject], internalAuthId = "id")

        registrationProgress.assetsStatus(userAnswers) mustBe None
      }

    }

    "render in-progress tag" when {

      "there are assets in user answers that are not complete" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
            .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value
            .set(WhatKindOfAssetPage(1), WhatKindOfAsset.Shares).success.value

        registrationProgress.assetsStatus(userAnswers).value mustBe InProgress
      }
    }

    "render complete tag" when {

      "there are assets in user answers that are complete" in {

        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value
          .set(AssetMoneyValuePage(0), "2000").success.value
          .set(AssetStatus(0), Completed).success.value
          .set(WhatKindOfAssetPage(1), WhatKindOfAsset.Shares).success.value
          .set(SharesInAPortfolioPage(1), true).success.value
          .set(SharePortfolioNamePage(1), "Portfolio").success.value
          .set(SharePortfolioQuantityInTrustPage(1), "30").success.value
          .set(SharePortfolioValueInTrustPage(1), "999999999999").success.value
          .set(SharePortfolioOnStockExchangePage(1), false).success.value
          .set(AssetStatus(1), Completed).success.value
          .set(AddAssetsPage, NoComplete).success.value

        registrationProgress.assetsStatus(userAnswers).value mustBe Completed
      }
    }
  }

  "All tasklist complete" when {

    "all entities marked as complete" in {

      when(registrationsRepository.getAllStatus(any())(any())).thenReturn(Future.successful(AllStatus.withAllComplete))

      val application = applicationBuilder().build()
      val registrationProgress = application.injector.instanceOf[RegistrationProgress]

      val userAnswers = emptyUserAnswers
        .set(DeceasedSettlorStatus, Status.Completed).success.value
        .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value
        .set(AssetMoneyValuePage(0), "2000").success.value
        .set(AssetStatus(0), Completed).success.value
        .set(WhatKindOfAssetPage(1), WhatKindOfAsset.Shares).success.value
        .set(SharesInAPortfolioPage(1), true).success.value
        .set(SharePortfolioNamePage(1), "Portfolio").success.value
        .set(SharePortfolioQuantityInTrustPage(1), "30").success.value
        .set(SharePortfolioValueInTrustPage(1), "999999999999").success.value
        .set(SharePortfolioOnStockExchangePage(1), false).success.value
        .set(AssetStatus(1), Completed).success.value
        .set(AddAssetsPage, NoComplete).success.value

      Await.result(registrationProgress.isTaskListComplete(userAnswers), Duration.Inf) mustBe true
    }
  }
}
