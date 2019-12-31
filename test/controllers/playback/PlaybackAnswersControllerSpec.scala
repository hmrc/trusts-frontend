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

package controllers.playback

import base.SpecBase
import connector.TrustConnector
import controllers.actions._
import models.core.pages.UKAddress
import models.playback.UserAnswers
import pages.playback.WhatIsTheUTRVariationPage
import pages.register.beneficiaries.charity._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolments}
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import utils.CharityBeneficiarySection
import utils.countryOptions.CountryOptions
import views.html.register.SummaryAnswerPageView
import play.api.inject.bind

class PlaybackAnswersControllerSpec extends SpecBase {

  val index = 0

  "PlaybackAnswersController" must {

    "return OK and the correct view for a GET" in {

      val countryOptions = injector.instanceOf[CountryOptions]

      val playbackAnswers = UserAnswers("internalId")
        .set(WhatIsTheUTRVariationPage, "0987654321").success.value

        .set(CharityBeneficiaryNamePage(0), "Charity Beneficiary").success.value
        .set(CharityBeneficiaryDiscretionYesNoPage(0), true).success.value
        .set(CharityBeneficiaryShareOfIncomePage(0), "10").success.value
        .set(CharityBeneficiaryAddressYesNoPage(0), true).success.value
        .set(CharityBeneficiaryAddressUKYesNoPage(0), true).success.value
        .set(CharityBeneficiaryAddressPage(0), UKAddress("line1", "line2", None, None, "NE11NE")).success.value

      val expectedSections = CharityBeneficiarySection(0, playbackAnswers, countryOptions).get

      val app = new GuiceApplicationBuilder()
        .overrides(
          bind[DataRequiredAction].to[DataRequiredActionImpl],
          bind[TrustConnector].toInstance(mock[TrustConnector]),
          bind[IdentifierAction].toInstance(new FakeIdentifyForRegistration(Organisation)(injectedParsers, trustsAuth, Enrolments(Set.empty))),
          bind[PlaybackAction].toInstance(new FakePlaybackAction()),
          bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(None)),
          bind[AffinityGroup].toInstance(Organisation)
        ).build()

      val request = FakeRequest(GET, controllers.playback.routes.PlaybackAnswerPageController.onPageLoad().url)

      val result = route(app, request).value

      val view = app.injector.instanceOf[SummaryAnswerPageView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(expectedSections, isAgent = false, agentClientRef = "")(fakeRequest, messages).toString

      app.stop()
    }

  }

}
