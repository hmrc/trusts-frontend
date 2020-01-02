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

import java.time.LocalDate

import base.SpecBase
import connector.TrustConnector
import controllers.actions._
import controllers.actions.playback.PlaybackIdentifierAction
import controllers.actions.register._
import mapping.TypeOfTrust.HeritageTrust
import mapping.playback.UserAnswersExtractor
import mapping.registration.TrustDetailsType
import models.playback.http._
import models.core.pages.UKAddress
import org.mockito.Matchers.{any, eq => eqTo}
import org.mockito.Mockito.when
import models.playback.{UserAnswers => PlaybackAnswers}
import models.core.UserAnswers
import pages.playback.WhatIsTheUTRVariationPage
import pages.register.beneficiaries.charity._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolments}
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import utils.CharityBeneficiarySection
import utils.countryOptions.CountryOptions
import play.api.inject.bind
import views.html.playback.PlaybackAnswersView

import scala.concurrent.Future

class PlaybackAnswersControllerSpec extends SpecBase {

  val index = 0

  "PlaybackAnswersController" must {

    "return OK and the correct view for a GET" in {

      val countryOptions = injector.instanceOf[CountryOptions]

      val userAnswers = UserAnswers(draftId = "draftId", internalAuthId = "id")
        .set(WhatIsTheUTRVariationPage, "0987654321").success.value

      val playbackAnswers = PlaybackAnswers("internalId")

        .set(CharityBeneficiaryNamePage(0), "Charity Beneficiary").success.value
        .set(CharityBeneficiaryDiscretionYesNoPage(0), true).success.value
        .set(CharityBeneficiaryShareOfIncomePage(0), "10").success.value
        .set(CharityBeneficiaryAddressYesNoPage(0), true).success.value
        .set(CharityBeneficiaryAddressUKYesNoPage(0), true).success.value
        .set(CharityBeneficiaryAddressPage(0), UKAddress("line1", "line2", None, None, "NE11NE")).success.value

      val expectedSections = CharityBeneficiarySection(0, playbackAnswers, countryOptions).get

      val trustData = GetTrust(
        MatchData("0987654321"),
        Correspondence(
          false,
          "Name",
          AddressType("line1", "line2", None, None, None, "UK"),
          None,
          "23456"
        ),
        Declaration(
          NameType("First", None, "Last"),
          AddressType("line1", "line2", None, None, None, "UK")
        ),
        DisplayTrust(
          TrustDetailsType(
            LocalDate.now(), None, None, None, HeritageTrust, None, None, None
          ),
          DisplayTrustEntitiesType(
            None,
            DisplayTrustBeneficiaryType(
              None, None, None,
              Some(List(DisplayTrustCharityType(
                "01",
                None,
                "CharityOrgName",
                Some(true),
                Some("50"),
                Some(DisplayTrustIdentificationOrgType(None, None, None)),
                LocalDate.now().toString
              ))), None, None, None
            ), None, DisplayTrustLeadTrusteeType(), None, None, None
          ),
          DisplayTrustAssets(None, None, None, None, None, None)
        )
      )

      val trustConnector = mock[TrustConnector]
      val userAnswersExtractor = mock[UserAnswersExtractor]

      when(
        trustConnector.playback(any())(any(), any())
      ) thenReturn Future.successful(Processed(trustData, "formBundle"))

      when(
        userAnswersExtractor.extract(any(), eqTo(trustData))
      ) thenReturn Right(playbackAnswers)

      val app = new GuiceApplicationBuilder()
        .overrides(
          bind[RegistrationDataRequiredAction].to[RegistrationDataRequiredActionImpl],
          bind[TrustConnector].toInstance(trustConnector),
          bind[RegistrationIdentifierAction].toInstance(new FakeIdentifyForRegistration(Organisation)(injectedParsers, trustsAuth, Enrolments(Set.empty))),
          bind[PlaybackIdentifierAction].toInstance(new FakePlaybackIdentifierAction()),
          bind[RegistrationDataRetrievalAction].toInstance(new FakeRegistrationDataRetrievalAction(Some(userAnswers))),
          bind[AffinityGroup].toInstance(Organisation),
          bind[UserAnswersExtractor].toInstance(userAnswersExtractor)
        ).build()

      val request = FakeRequest(GET, controllers.playback.routes.PlaybackAnswerPageController.onPageLoad().url)

      val result = route(app, request).value

      val view = app.injector.instanceOf[PlaybackAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(expectedSections)(fakeRequest, messages).toString

      app.stop()
    }

  }

}
