/*
 * Copyright 2019 HM Revenue & Customs
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

package controllers

import base.SpecBase
import forms.AddAssetsFormProvider
import models.Status.Completed
import models.WhatKindOfAsset.{Money, Shares}
import models.{AddAssets, NormalMode, ShareClass}
import navigation.{FakeNavigator, Navigator}
import pages.entitystatus.AssetStatus
import pages.shares._
import pages.{AssetMoneyValuePage, WhatKindOfAssetPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.AddRow
import views.html.AddAssetsView

class AddAssetsControllerSpec extends SpecBase {

  lazy val addAssetsRoute = routes.AddAssetsController.onPageLoad(fakeDraftId).url

  val formProvider = new AddAssetsFormProvider()
  val form = formProvider()

  val assets = List(
    AddRow("£4800", typeLabel = "Money", "#", "#"),
    AddRow("Share Company Name", typeLabel = "Shares", "#", "#")
  )

  val userAnswersWithAssetsComplete = emptyUserAnswers
    .set(WhatKindOfAssetPage(0), Money).success.value
    .set(AssetMoneyValuePage(0), "4800").success.value
    .set(AssetStatus(0), Completed).success.value
    .set(WhatKindOfAssetPage(1), Shares).success.value
    .set(SharesInAPortfolioPage(1), false).success.value
    .set(ShareCompanyNamePage(1), "Share Company Name").success.value
    .set(SharesOnStockExchangePage(1), true).success.value
    .set(ShareClassPage(1), ShareClass.Ordinary).success.value
    .set(ShareQuantityInTrustPage(1), "1000").success.value
    .set(ShareValueInTrustPage(1), "10").success.value
    .set(AssetStatus(1), Completed).success.value

  "AddAssets Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswersWithAssetsComplete)).build()

      val request = FakeRequest(GET, addAssetsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AddAssetsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode,fakeDraftId, Nil, assets, "You have added 2 assets")(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(userAnswersWithAssetsComplete)).build()

      val request =
        FakeRequest(POST, addAssetsRoute)
          .withFormUrlEncodedBody(("value", AddAssets.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(userAnswersWithAssetsComplete)).build()

      val request =
        FakeRequest(POST, addAssetsRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[AddAssetsView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode,fakeDraftId, Nil, assets, "You have added 2 assets")(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, addAssetsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, addAssetsRoute)
          .withFormUrlEncodedBody(("value", AddAssets.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
