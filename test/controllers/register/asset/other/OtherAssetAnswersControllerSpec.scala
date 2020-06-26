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

package controllers.register.asset.other

import base.RegistrationSpecBase
import models.core.UserAnswers
import models.registration.pages.WhatKindOfAsset.Other
import pages.register.asset.WhatKindOfAssetPage
import pages.register.asset.other._
import play.api.Application
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.register.asset.other.OtherAssetAnswersView

import scala.concurrent.Future

class OtherAssetAnswersControllerSpec extends RegistrationSpecBase {

  "OtherAssetAnswersController" must {

    val index: Int = 0
    val description: String = "Description"

    lazy val answersRoute = routes.OtherAssetAnswersController.onPageLoad(index, fakeDraftId).url

    "return OK and the correct view for a GET" in {

      val answers: UserAnswers =
        emptyUserAnswers
          .set(WhatKindOfAssetPage(index), Other).success.value
          .set(OtherAssetDescriptionPage(index), description).success.value
          .set(OtherAssetValuePage(index), "4000").success.value

      val countryOptions: CountryOptions = injector.instanceOf[CountryOptions]

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers, fakeDraftId, canEdit = true)

      val expectedSections = Seq(
        AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.whatKindOfAsset(index).value,
            checkYourAnswersHelper.otherAssetDescription(index).value,
            checkYourAnswersHelper.otherAssetValue(index, description).value
          )
        )
      )

      val application: Application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, answersRoute)

      val result: Future[Result] = route(application, request).value

      val view: OtherAssetAnswersView = application.injector.instanceOf[OtherAssetAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(index, fakeDraftId, expectedSections)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to add assets on submission" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(OtherAssetDescriptionPage(index), description).success.value

      val application: Application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(POST, answersRoute)

      val result: Future[Result] = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.register.asset.routes.AddAssetsController.onPageLoad(fakeDraftId).url

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, answersRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.register.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, answersRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.register.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
