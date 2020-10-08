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

import java.time.LocalDateTime

import models.registration.pages.AddAssets.NoComplete
import models.registration.pages.Status.Completed
import models.registration.pages._
import pages.entitystatus._
import pages.register.{RegistrationSubmissionDatePage, RegistrationTRNPage}
import pages.register.asset._
import pages.register.asset.money._
import pages.register.asset.property_or_land._
import pages.register.asset.shares._
import uk.gov.hmrc.http.HeaderCarrier
import utils.AccessibilityHelper._
import utils.print.register.PrintUserAnswersHelper
import utils.{DateFormatter, TestUserAnswers}
import views.behaviours.ViewBehaviours
import views.html.register.ConfirmationAnswerPageView

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ConfirmationAnswerPageViewSpec extends ViewBehaviours {
  val index = 0

  "ConfirmationAnswerPage view" must {

    val userAnswers =
      TestUserAnswers.emptyUserAnswers
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

    val formatter = injector.instanceOf[DateFormatter]

    val trnDateTime: String = formatter.formatDate(LocalDateTime.of(2010, 10, 10, 13, 10, 10))
    val name = "First Last"
    val yes = "Yes"
    val no = "No"

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val app = applicationBuilder().build()

    val helper = app.injector.instanceOf[PrintUserAnswersHelper]

    val viewFuture = helper.summary(fakeDraftId, userAnswers).map {
      sections =>
        val view = viewFor[ConfirmationAnswerPageView](Some(userAnswers))

        view.apply(sections, formatReferenceNumber("XNTRN000000001"), trnDateTime)(fakeRequest, messages)
    }

    val view = Await.result(viewFuture, Duration.Inf)

    val doc = asDocument(view)

    behave like normalPage(view, None, "confirmationAnswerPage")

    "assert header content" in {
      assertContainsText(doc, messages("confirmationAnswerPage.paragraph1", formatReferenceNumber("XNTRN000000001")))
      assertContainsText(doc, messages("confirmationAnswerPage.paragraph2", trnDateTime))
    }

    "assert correct number of headers and subheaders" in {
      val wrapper = doc.getElementById("wrapper")
      val headers = wrapper.getElementsByTag("h2")
      val subHeaders = wrapper.getElementsByTag("h3")

      headers.size mustBe 1
      subHeaders.size mustBe 3
    }

  }
}
