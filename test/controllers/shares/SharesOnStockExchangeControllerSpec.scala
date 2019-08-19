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

package controllers.shares

import base.SpecBase
import controllers.IndexValidation
import forms.shares.SharesOnStockExchangeFormProvider
import generators.ModelGenerators
import models.NormalMode
import navigation.{FakeNavigator, Navigator}
import org.scalacheck.Arbitrary.arbitrary
import pages.shares.{ShareCompanyNamePage, SharesOnStockExchangePage}
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import views.html.shares.SharesOnStockExchangeView

class SharesOnStockExchangeControllerSpec extends SpecBase with ModelGenerators with IndexValidation {

  val formProvider = new SharesOnStockExchangeFormProvider()
  val form = formProvider()
  val index: Int = 0
  val companyName = "Company"

  lazy val sharesOnStockExchangeRoute = controllers.shares.routes.SharesOnStockExchangeController.onPageLoad(NormalMode, index, fakeDraftId).url

  "SharesOnStockExchange Controller" must {

    "return OK and the correct view for a GET" in {

      val ua = emptyUserAnswers.set(ShareCompanyNamePage(index), companyName).success.value

      val application = applicationBuilder(userAnswers = Some(ua)).build()

      val request = FakeRequest(GET, sharesOnStockExchangeRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[SharesOnStockExchangeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, fakeDraftId, index, companyName)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val ua = emptyUserAnswers.set(ShareCompanyNamePage(index), companyName).success.value
        .set(SharesOnStockExchangePage(index), true).success.value

      val application = applicationBuilder(userAnswers = Some(ua)).build()

      val request = FakeRequest(GET, sharesOnStockExchangeRoute)

      val view = application.injector.instanceOf[SharesOnStockExchangeView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), NormalMode, fakeDraftId, index, companyName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val ua = emptyUserAnswers.set(ShareCompanyNamePage(index), "Share").success.value

      val application =
        applicationBuilder(userAnswers = Some(ua)).build()

      val request =
        FakeRequest(POST, sharesOnStockExchangeRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val ua = emptyUserAnswers.set(ShareCompanyNamePage(index), companyName).success.value

      val application = applicationBuilder(userAnswers = Some(ua)).build()

      val request =
        FakeRequest(POST, sharesOnStockExchangeRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[SharesOnStockExchangeView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, fakeDraftId, index, companyName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, sharesOnStockExchangeRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, sharesOnStockExchangeRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to ShareCompanyName when company name is not answered" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, sharesOnStockExchangeRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.shares.routes.ShareCompanyNameController.onPageLoad(NormalMode, index, fakeDraftId).url

      application.stop()
    }
  }

  "for a GET" must {

    def getForIndex(index: Int) : FakeRequest[AnyContentAsEmpty.type] = {
      val route =
        controllers.shares.routes.SharesOnStockExchangeController.onPageLoad(NormalMode, index, fakeDraftId).url

      FakeRequest(GET, route)
    }

    validateIndex(
      arbitrary[Boolean],
      SharesOnStockExchangePage.apply,
      getForIndex
    )

  }

  "for a POST" must {
    def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

      val route =
        controllers.shares.routes.SharesOnStockExchangeController.onPageLoad(NormalMode, index, fakeDraftId).url

      FakeRequest(POST, route)
        .withFormUrlEncodedBody(("value", "true"))
    }

    validateIndex(
      arbitrary[Boolean],
      SharesOnStockExchangePage.apply,
      postForIndex
    )
  }
}
