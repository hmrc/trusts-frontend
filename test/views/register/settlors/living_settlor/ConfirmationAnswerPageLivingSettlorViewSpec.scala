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

package views.register.settlors.living_settlor

import java.time.{LocalDate, LocalDateTime}

import models.core.pages.{FullName, IndividualOrBusiness, UKAddress}
import models.registration.pages.AddAssets.NoComplete
import models.registration.pages.Status.Completed
import models.registration.pages.TrusteesBasedInTheUK.UKBasedTrustees
import models.registration.pages._
import pages.entitystatus._
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.property_or_land._
import pages.register.asset.shares._
import pages.register.asset.{AddAssetsPage, WhatKindOfAssetPage}
import pages.register.settlors.SetUpAfterSettlorDiedYesNoPage
import pages.register.settlors.living_settlor._
import pages.register.settlors.living_settlor.trust_type.{HoldoverReliefYesNoPage, KindOfTrustPage}
import pages.register.trust_details._
import pages.register.{RegistrationSubmissionDatePage, RegistrationTRNPage}
import uk.gov.hmrc.http.HeaderCarrier
import utils.AccessibilityHelper._
import utils.print.register.PrintUserAnswersHelper
import utils.{DateFormatter, TestUserAnswers}
import views.behaviours.ViewBehaviours
import views.html.register.ConfirmationAnswerPageView

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ConfirmationAnswerPageLivingSettlorViewSpec extends ViewBehaviours {
  private val index = 0

  private val userAnswers =
    TestUserAnswers.emptyUserAnswers
      .set(TrustNamePage, "New Trust").success.value
      .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
      .set(GovernedInsideTheUKPage, true).success.value
      .set(AdministrationInsideUKPage, true).success.value
      .set(TrusteesBasedInTheUKPage, UKBasedTrustees).success.value
      .set(EstablishedUnderScotsLawPage, true).success.value
      .set(TrustResidentOffshorePage, false).success.value
      .set(TrustDetailsStatus, Completed).success.value

      .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
      .set(KindOfTrustPage, KindOfTrust.Intervivos).success.value
      .set(HoldoverReliefYesNoPage, true).success.value
      .set(SettlorIndividualOrBusinessPage(index),IndividualOrBusiness.Individual).success.value
      .set(SettlorIndividualNamePage(index), FullName("First", None, "Last")).success.value
      .set(SettlorIndividualDateOfBirthYesNoPage(index), true).success.value
      .set(SettlorIndividualDateOfBirthPage(index), LocalDate.of(2010, 10, 10)).success.value
      .set(SettlorIndividualNINOYesNoPage(index), true).success.value
      .set(SettlorIndividualNINOPage(index), "AB123456C").success.value
      .set(LivingSettlorStatus(index), Status.Completed).success.value

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


  private val formatter = injector.instanceOf[DateFormatter]

  private val trnDateTime : String = formatter.formatDate(LocalDateTime.of(2010, 10, 10, 13, 10, 10))
  private val name = "First Last"
  private val trusteeName = "TrusteeFirst TrusteeLast"
  private val yes = "Yes"
  private val no = "No"

  private val application = applicationBuilder().build()

  private val helper = application.injector.instanceOf[PrintUserAnswersHelper]

  private implicit val hc: HeaderCarrier = HeaderCarrier()

  private val viewFuture = helper.summary(fakeDraftId, userAnswers).map {
    sections =>
      val view = viewFor[ConfirmationAnswerPageView](Some(userAnswers))

      view.apply(sections, formatReferenceNumber("XNTRN000000001"), trnDateTime)(fakeRequest, messages)
  }

  private val view = Await.result(viewFuture, Duration.Inf)

  private val doc = asDocument(view)

  "ConfirmationAnswerPage view" must {

    behave like normalPage(view, None, "confirmationAnswerPage")

    "assert header content" in {
      assertContainsText(doc, messages("confirmationAnswerPage.paragraph1", formatReferenceNumber("XNTRN000000001")))
      assertContainsText(doc, messages("confirmationAnswerPage.paragraph2", trnDateTime))
    }

    "assert correct number of headers and subheaders" in {
      val wrapper = doc.getElementById("wrapper")
      val headers = wrapper.getElementsByTag("h2")
      val subHeaders = wrapper.getElementsByTag("h3")

      headers.size mustBe 2
      subHeaders.size mustBe 1
    }

    "assert question labels for Trusts" in {
      assertContainsQuestionAnswerPair(doc, messages("trustName.checkYourAnswersLabel"), "New Trust")
      assertContainsQuestionAnswerPair(doc, messages("whenTrustSetup.checkYourAnswersLabel"), "10 October 2010")
    }

    "assert question labels for Settlors" in {
      assertContainsQuestionAnswerPair(doc, messages("setUpAfterSettlorDied.checkYourAnswersLabel"), no)
      assertContainsQuestionAnswerPair(doc, messages("kindOfTrust.checkYourAnswersLabel"), "A trust created during their lifetime to gift or transfer assets")
      assertContainsQuestionAnswerPair(doc, messages("holdoverReliefYesNo.checkYourAnswersLabel"), yes)
      assertContainsQuestionAnswerPair(doc, messages("settlorIndividualOrBusiness.checkYourAnswersLabel"), "Individual")
      assertContainsQuestionAnswerPair(doc, messages("settlorIndividualName.checkYourAnswersLabel"), name)
      assertContainsQuestionAnswerPair(doc, messages("settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel", name), yes)
      assertContainsQuestionAnswerPair(doc, messages("settlorIndividualDateOfBirth.checkYourAnswersLabel", name), "10 October 2010")
      assertContainsQuestionAnswerPair(doc, messages("settlorIndividualNINOYesNo.checkYourAnswersLabel", name), yes)
      assertContainsQuestionAnswerPair(doc, messages("settlorIndividualNINO.checkYourAnswersLabel", name), "AB 12 34 56 C")
    }
  }
}
