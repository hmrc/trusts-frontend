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

package navigation.navigators.registration

import base.RegistrationSpecBase
import controllers.register.asset.routes
import generators.Generators
import models.NormalMode
import models.core.UserAnswers
import models.core.pages.UKAddress
import models.registration.pages.AddAssets
import models.registration.pages.WhatKindOfAsset.{Business, Money, Other, Partnership, PropertyOrLand, Shares}
import navigation.Navigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.asset.business._
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.other.{OtherAssetDescriptionPage, OtherAssetValuePage}
import pages.register.asset.shares._
import pages.register.asset.{AddAnAssetYesNoPage, AddAssetsPage, WhatKindOfAssetPage}

trait AssetRoutes {

  self: ScalaCheckPropertyChecks with Generators with RegistrationSpecBase =>

  def assetRoutes()(implicit navigator: Navigator) = {

    "go to WhatKindOfAssetPage from from AddAnAssetYesNoPage when selected Yes" in {
      val index = 0

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(AddAnAssetYesNoPage, true).success.value

          navigator.nextPage(AddAnAssetYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(routes.WhatKindOfAssetController.onPageLoad(NormalMode, index, fakeDraftId))
      }
    }

    "go to RegistrationProgress from from AddAnAssetYesNoPage when selected No" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(AddAnAssetYesNoPage, false).success.value

          navigator.nextPage(AddAnAssetYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(controllers.register.routes.TaskListController.onPageLoad(fakeDraftId))
      }
    }

    "money assets" must {

      "go to AssetMoneyValuePage from WhatKindOfAsset page when the money option is selected" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(WhatKindOfAssetPage(index), Money).success.value

            navigator.nextPage(WhatKindOfAssetPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(controllers.register.asset.money.routes.AssetMoneyValueController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "go to PropertyOrLandAddressYesNoController from WhatKindOfAsset page when the PropertyOrLand option is selected" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(WhatKindOfAssetPage(index), PropertyOrLand).success.value

            navigator.nextPage(WhatKindOfAssetPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(controllers.register.asset.property_or_land.routes.PropertyOrLandAddressYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }


      "go to AddAssetsPage from AssetMoneyValue page when the amount submitted" in {

        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(WhatKindOfAssetPage(index), Money).success.value

            navigator.nextPage(AssetMoneyValuePage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(routes.AddAssetsController.onPageLoad(fakeDraftId))

        }
      }

      "go to AddAssetPage from ShareAnswerPage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(ShareAnswerPage, NormalMode, fakeDraftId)(userAnswers)
              .mustBe(routes.AddAssetsController.onPageLoad(fakeDraftId))

        }
      }

    }

    "share assets" must {

      "go to SharesInAPortfolio from WhatKindOfAsset when share option is selected" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(WhatKindOfAssetPage(index), Shares).success.value

            navigator.nextPage(WhatKindOfAssetPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(controllers.register.asset.shares.routes.SharesInAPortfolioController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "for a portfolio" must {

        "go to SharePortfolioName from SharesInAPortfolio when user answers yes" in {
          val index = 0

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(SharesInAPortfolioPage(index), true).success.value

              navigator.nextPage(SharesInAPortfolioPage(index), NormalMode, fakeDraftId)(answers)
                .mustBe(controllers.register.asset.shares.routes.SharePortfolioNameController.onPageLoad(NormalMode, index, fakeDraftId))
          }
        }

        "go to SharePortfolioOnStockExchange from SharePortfolioName" in {
          val index = 0

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              navigator.nextPage(SharePortfolioNamePage(index), NormalMode, fakeDraftId)(userAnswers)
                .mustBe(controllers.register.asset.shares.routes.SharePortfolioOnStockExchangeController.onPageLoad(NormalMode, index, fakeDraftId))
          }
        }

        "go to SharePortfolioQuantityInTrust from SharePortfolioOnStockExchange" in {
          val index = 0

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              navigator.nextPage(SharePortfolioOnStockExchangePage(index), NormalMode, fakeDraftId)(userAnswers)
                .mustBe(controllers.register.asset.shares.routes.SharePortfolioQuantityInTrustController.onPageLoad(NormalMode, index, fakeDraftId))
          }
        }

        "go to SharePortfolioValueInTrust from SharePortfolioQuantityInTrust" in {
          val index = 0

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              navigator.nextPage(SharePortfolioQuantityInTrustPage(index), NormalMode, fakeDraftId)(userAnswers)
                .mustBe(controllers.register.asset.shares.routes.SharePortfolioValueInTrustController.onPageLoad(NormalMode, index, fakeDraftId))
          }
        }

        "go to ShareAnswers from SharePortfolioValueInTrust" in {
          val index = 0

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              navigator.nextPage(SharePortfolioValueInTrustPage(index), NormalMode, fakeDraftId)(userAnswers)
                .mustBe(controllers.register.asset.shares.routes.ShareAnswerController.onPageLoad(index, fakeDraftId))
          }
        }

      }

      "for shares" must {

        "go to ShareCompanyName from SharesInAPortfolio when user answers no" in {
          val index = 0

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(SharesInAPortfolioPage(index), false).success.value

              navigator.nextPage(SharesInAPortfolioPage(index), NormalMode, fakeDraftId)(answers)
                .mustBe(controllers.register.asset.shares.routes.ShareCompanyNameController.onPageLoad(NormalMode, index, fakeDraftId))
          }
        }

        "go to SharesOnStockExchange from ShareCompanyName" in {
          val index = 0

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              navigator.nextPage(ShareCompanyNamePage(index), NormalMode, fakeDraftId)(userAnswers)
                .mustBe(controllers.register.asset.shares.routes.SharesOnStockExchangeController.onPageLoad(NormalMode, index, fakeDraftId))
          }
        }

        "go to ShareClass from SharesOnStockExchange" in {
          val index = 0

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              navigator.nextPage(SharesOnStockExchangePage(index), NormalMode, fakeDraftId)(userAnswers)
                .mustBe(controllers.register.asset.shares.routes.ShareClassController.onPageLoad(NormalMode, index, fakeDraftId))
          }
        }

        "go to ShareQuantityInTrust from ShareClass" in {
          val index = 0

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              navigator.nextPage(ShareClassPage(index), NormalMode, fakeDraftId)(userAnswers)
                .mustBe(controllers.register.asset.shares.routes.ShareQuantityInTrustController.onPageLoad(NormalMode, index, fakeDraftId))
          }
        }

        "go to ShareValueInTrust from ShareQuantityInTrust" in {
          val index = 0

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              navigator.nextPage(ShareQuantityInTrustPage(index), NormalMode, fakeDraftId)(userAnswers)
                .mustBe(controllers.register.asset.shares.routes.ShareValueInTrustController.onPageLoad(NormalMode, index, fakeDraftId))
          }
        }

        "go to ShareAnswers from ShareValueInTrust" in {
          val index = 0

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              navigator.nextPage(ShareValueInTrustPage(index), NormalMode, fakeDraftId)(userAnswers)
                .mustBe(controllers.register.asset.shares.routes.ShareAnswerController.onPageLoad(index, fakeDraftId))
          }
        }

      }

    }

    "other assets" must {

      "go to other asset description from WhatKindOfAsset when other is selected" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(WhatKindOfAssetPage(index), Other).success.value

            navigator.nextPage(WhatKindOfAssetPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(controllers.register.asset.other.routes.OtherAssetDescriptionController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "go to other asset value from other asset description" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(OtherAssetDescriptionPage(index), "Description").success.value

            navigator.nextPage(OtherAssetDescriptionPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(controllers.register.asset.other.routes.OtherAssetValueController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "go to check answers from other asset value" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(OtherAssetValuePage(index), "4000").success.value

            navigator.nextPage(OtherAssetValuePage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(controllers.register.asset.other.routes.OtherAssetAnswersController.onPageLoad(index, fakeDraftId))
        }
      }
    }

    "partnership assets" must {

      "go to partnership asset description from WhatKindOfAsset when partnership is selected" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(WhatKindOfAssetPage(index), Partnership).success.value

            navigator.nextPage(WhatKindOfAssetPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(controllers.register.asset.partnership.routes.PartnershipDescriptionController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

    }

    "add another asset" must {

      "go to the WhatKindOfAssetPage from AddAssetsPage when selected add them now" in {

        val answers = emptyUserAnswers
          .set(WhatKindOfAssetPage(0), Money).success.value
          .set(AddAssetsPage, AddAssets.YesNow).success.value

        navigator.nextPage(AddAssetsPage, NormalMode, fakeDraftId)(answers)
          .mustBe(routes.WhatKindOfAssetController.onPageLoad(NormalMode, 1, fakeDraftId))
      }
    }

    "go to RegistrationProgress from AddAssetsPage when selecting add them later" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = emptyUserAnswers
            .set(WhatKindOfAssetPage(0), Money).success.value
            .set(AddAssetsPage, AddAssets.YesLater).success.value

          navigator.nextPage(AddAssetsPage, NormalMode, fakeDraftId)(answers)
            .mustBe(controllers.register.routes.TaskListController.onPageLoad(fakeDraftId))
      }
    }

    "go to RegistrationProgress from AddAssetsPage when selecting no complete" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = emptyUserAnswers
            .set(WhatKindOfAssetPage(0), Money).success.value
            .set(AddAssetsPage, AddAssets.NoComplete).success.value

          navigator.nextPage(AddAssetsPage, NormalMode, fakeDraftId)(answers)
            .mustBe(controllers.register.routes.TaskListController.onPageLoad(fakeDraftId))
      }
    }

    "business assets" must {

      "go to AssetNamePage from WhatKindOfAsset page when the business option is selected" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(WhatKindOfAssetPage(index), Business).success.value

            navigator.nextPage(WhatKindOfAssetPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(controllers.register.asset.business.routes.BusinessNameController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "go to AssetDescription from AssetNamepage" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(BusinessNamePage(index), "Test").success.value

            navigator.nextPage(BusinessNamePage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(controllers.register.asset.business.routes.BusinessDescriptionController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "go to AssetAddressUkYesNo from AssetDescription" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(BusinessDescriptionPage(index), "Test Test Test").success.value

            navigator.nextPage(BusinessDescriptionPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(controllers.register.asset.business.routes.BusinessAddressUkYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "go to AssetUkAddress from AssetAddressUkYesNo" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(BusinessAddressUkYesNoPage(index), true).success.value

            navigator.nextPage(BusinessAddressUkYesNoPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(controllers.register.asset.business.routes.BusinessUkAddressController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "go to AssetInternationalAddress from AssetAddressUkYesNo" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(BusinessAddressUkYesNoPage(index), false).success.value

            navigator.nextPage(BusinessAddressUkYesNoPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(controllers.register.asset.business.routes.BusinessInternationalAddressController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "go to CurrentValue from AssetUkAddress" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(BusinessUkAddressPage(index), UKAddress("Test line 1", "Test line 2", None, None, "AA111AA")).success.value

            navigator.nextPage(BusinessUkAddressPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(controllers.register.asset.business.routes.BusinessValueController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "go to AssetAnswer from CurrentValue" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(BusinessValuePage(index), "12").success.value

            navigator.nextPage(BusinessValuePage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(controllers.register.asset.business.routes.BusinessAnswersController.onPageLoad(index, fakeDraftId))
        }
      }
    }
  }
}
