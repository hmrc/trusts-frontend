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

package views.register

import java.time.{LocalDate, LocalDateTime}

import models.core.pages.{FullName, UKAddress}
import models.registration.pages.AddAssets.NoComplete
import models.registration.pages.Status.Completed
import models.registration.pages._
import pages.entitystatus._
import pages.register._
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.property_or_land._
import pages.register.asset.shares._
import pages.register.asset.{AddAssetsPage, WhatKindOfAssetPage}
import pages.register.settlors.SetUpAfterSettlorDiedYesNoPage
import pages.register.settlors.deceased_settlor._
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUserAnswers
import utils.print.register.PrintUserAnswersHelper
import views.behaviours.ViewBehaviours
import views.html.register.SummaryAnswerPageView

class SummaryAnswerPageViewSpec extends ViewBehaviours {
  val index = 0

  "SummaryAnswerPage view" must {

    val userAnswers =
      TestUserAnswers.emptyUserAnswers
        .set(SetUpAfterSettlorDiedYesNoPage, true).success.value
        .set(SettlorsNamePage, FullName("First", None, "Last")).success.value
        .set(SettlorDateOfDeathYesNoPage, true).success.value
        .set(SettlorDateOfDeathPage, LocalDate.of(2010, 10, 10)).success.value
        .set(SettlorDateOfBirthYesNoPage, true).success.value
        .set(SettlorsDateOfBirthPage, LocalDate.of(2010, 10, 10)).success.value
        .set(SettlorsNationalInsuranceYesNoPage, true).success.value
        .set(SettlorNationalInsuranceNumberPage, "AB123456C").success.value
        .set(SettlorsLastKnownAddressYesNoPage, true).success.value
        .set(WasSettlorsAddressUKYesNoPage, true).success.value
        .set(SettlorsUKAddressPage, UKAddress("Line1", "Line2", None, None, "NE1 1ZZ")).success.value
        .set(DeceasedSettlorStatus, Status.Completed).success.value

        .set(WhatKindOfAssetPage(index), WhatKindOfAsset.Money).success.value
        .set(AssetMoneyValuePage(index), "100").success.value
        .set(AssetStatus(index), Completed).success.value
        .set(WhatKindOfAssetPage(1), WhatKindOfAsset.Shares).success.value
        .set(SharesInAPortfolioPage(1), true).success.value
        .set(SharePortfolioNamePage(1), "Company").success.value
        .set(SharePortfolioOnStockExchangePage(1), true).success.value
        .set(SharePortfolioQuantityInTrustPage(1), "1234").success.value
        .set(SharePortfolioValueInTrustPage(1), "4000").success.value
        .set(AssetStatus(1), Completed).success.value
        .set(WhatKindOfAssetPage(2), WhatKindOfAsset.PropertyOrLand).success.value
        .set(PropertyOrLandAddressYesNoPage(2), false).success.value
        .set(PropertyOrLandDescriptionPage(2), "Town House").success.value
        .set(PropertyOrLandTotalValuePage(2), "10000").success.value
        .set(TrustOwnAllThePropertyOrLandPage(2), false).success.value
        .set(PropertyLandValueTrustPage(2), "10").success.value
        .set(AssetStatus(2), Completed).success.value
        .set(AddAssetsPage, NoComplete).success.value

        .set(RegistrationTRNPage, "XNTRN000000001").success.value
        .set(RegistrationSubmissionDatePage, LocalDateTime.of(2010, 10, 10, 13, 10, 10)).success.value

    val name = "First Last"
    val yes = "Yes"

    val view = viewFor[SummaryAnswerPageView](Some(userAnswers))

    val app = applicationBuilder().build()

    val helper = app.injector.instanceOf[PrintUserAnswersHelper]

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val summary = helper.summary(fakeDraftId, userAnswers)

    val orgDoc = {
      summary.map {
        sections =>
          val applyOrganisationView = view.apply(sections, isAgent = false, "")(fakeRequest, messages)

          behave like normalPage(applyOrganisationView, None, "summaryAnswerPage", "paragraph1", "paragraph2")

          asDocument(applyOrganisationView)
      }
    }

    val agentDoc = {
      summary.map {
        sections =>
          val applyAgentView = view.apply(sections, isAgent = true, "agentClientReference")(fakeRequest, messages)

          asDocument(applyAgentView)
      }
    }

    "assert header content for Agent user" in {
      agentDoc.map(assertContainsText(_, messages("answerPage.agentClientRef", "agentClientReference")))
    }

    "assert correct number of headers and subheaders for Agent user" in {
      agentDoc.map {
        doc =>
          val wrapper = doc.getElementById("wrapper")
          val headers = wrapper.getElementsByTag("h2")
          val subHeaders = wrapper.getElementsByTag("h3")

          headers.size mustBe 5
          subHeaders.size mustBe 3
      }
    }

    "assert correct number of headers and subheaders for Organisation user" in {
      orgDoc.map {
        doc =>
          val wrapper = doc.getElementById("wrapper")
          val headers = wrapper.getElementsByTag("h2")
          val subHeaders = wrapper.getElementsByTag("h3")

          headers.size mustBe 4
          subHeaders.size mustBe 3
      }
    }

    "assert question labels for Settlors" in {
      orgDoc.map {
        doc =>
          assertContainsQuestionAnswerPair(doc, messages("setUpAfterSettlorDied.checkYourAnswersLabel"), yes)
          assertContainsQuestionAnswerPair(doc, messages("settlorsName.checkYourAnswersLabel"), name)
          assertContainsQuestionAnswerPair(doc, messages("settlorDateOfBirthYesNo.checkYourAnswersLabel", name), yes)
          assertContainsQuestionAnswerPair(doc, messages("settlorsDateOfBirth.checkYourAnswersLabel", name), "10 October 2010")
          assertContainsQuestionAnswerPair(doc, messages("settlorDateOfDeathYesNo.checkYourAnswersLabel", name), yes)
          assertContainsQuestionAnswerPair(doc, messages("settlorDateOfDeath.checkYourAnswersLabel", name), "10 October 2010")
          assertContainsQuestionAnswerPair(doc, messages("settlorsNationalInsuranceYesNo.checkYourAnswersLabel", name), yes)
          assertContainsQuestionAnswerPair(doc, messages("settlorNationalInsuranceNumber.checkYourAnswersLabel", name), "AB 12 34 56 C")
          assertContainsQuestionAnswerPair(doc, messages("settlorsLastKnownAddressYesNo.checkYourAnswersLabel", name), yes)
          assertContainsQuestionAnswerPair(doc, messages("wasSettlorsAddressUKYesNo.checkYourAnswersLabel", name), yes)
          assertContainsQuestionAnswerPair(doc, messages("settlorsUKAddress.checkYourAnswersLabel", name), "Line1 Line2 NE1 1ZZ")
      }
    }
  }
}
