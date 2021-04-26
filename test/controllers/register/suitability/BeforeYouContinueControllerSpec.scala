/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.register.suitability

import base.RegistrationSpecBase
import navigation.registration.TaskListNavigator
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.TrustHaveAUTRPage
import pages.register.suitability.{ExpressTrustYesNoPage, TrustTaxableYesNoPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.FeatureFlagService
import uk.gov.hmrc.auth.core.AffinityGroup
import views.html.register.suitability.{BeforeYouContinueNonTaxAgentView, BeforeYouContinueNonTaxableView, BeforeYouContinueTaxableView, BeforeYouContinueView}

import scala.concurrent.Future

class BeforeYouContinueControllerSpec extends RegistrationSpecBase with ScalaCheckPropertyChecks {

  private lazy val beforeYouContinueRoute: String = routes.BeforeYouContinueController.onPageLoad(fakeDraftId).url

  private val navigator: TaskListNavigator = mock[TaskListNavigator]

  private val mockFeatureFlagService = mock[FeatureFlagService]

  "BeforeYouContinue Controller" must {

    "in 4mld mode" when {

      "return OK and the correct view for a taxable journey GET" in {

        when(mockFeatureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(false))

        val answers = emptyUserAnswers.set(TrustTaxableYesNoPage, true).success.value
          .set(TrustHaveAUTRPage, false).success.value

        val application = applicationBuilder(userAnswers = Some(answers))
          .overrides(bind[FeatureFlagService].toInstance(mockFeatureFlagService))
          .build()

        val request = FakeRequest(GET, beforeYouContinueRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[BeforeYouContinueView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(fakeDraftId)(request, messages).toString

        application.stop()
      }

    }

    "in 5mld mode" when {

      "return OK and the correct view for a taxable journey GET" in {

        when(mockFeatureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(true))

        val answers = emptyUserAnswers.set(TrustTaxableYesNoPage, true).success.value
          .set(TrustHaveAUTRPage, true).success.value

        val application = applicationBuilder(userAnswers = Some(answers))
          .overrides(bind[FeatureFlagService].toInstance(mockFeatureFlagService))
          .build()

        val request = FakeRequest(GET, beforeYouContinueRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[BeforeYouContinueTaxableView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(fakeDraftId)(request, messages).toString

        application.stop()
      }

    }

    "return OK and the correct view for a non taxable journey GET" in {

      when(mockFeatureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(true))

      val answers = emptyUserAnswers.set(TrustTaxableYesNoPage, false).success.value

      val application = applicationBuilder(userAnswers = Some(answers), affinityGroup = AffinityGroup.Organisation)
        .overrides(bind[FeatureFlagService].toInstance(mockFeatureFlagService))
        .build()

      val request = FakeRequest(GET, beforeYouContinueRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[BeforeYouContinueNonTaxableView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeDraftId)(request, messages).toString

      application.stop()
    }

    "return OK and the correct view for a non taxable agent journey GET" in {

      when(mockFeatureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(true))

      val answers = emptyUserAnswers.set(TrustTaxableYesNoPage, false).success.value

      val application = applicationBuilder(userAnswers = Some(answers), affinityGroup = AffinityGroup.Agent)
        .overrides(bind[FeatureFlagService].toInstance(mockFeatureFlagService))
        .build()

      val request = FakeRequest(GET, beforeYouContinueRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[BeforeYouContinueNonTaxAgentView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeDraftId)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" when {

      "express, non-taxable trust with non-taxable access code feature enabled" in {

        when(mockFeatureFlagService.isNonTaxableAccessCodeEnabled()(any(), any()))
          .thenReturn(Future.successful(true))

        val answers = emptyUserAnswers
          .set(ExpressTrustYesNoPage, true).success.value
          .set(TrustTaxableYesNoPage, false).success.value

        val application = applicationBuilder(userAnswers = Some(answers), affinityGroup = AffinityGroup.Agent)
          .overrides(bind[FeatureFlagService].toInstance(mockFeatureFlagService))
          .build()

        val request = FakeRequest(POST, beforeYouContinueRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          routes.NonTaxableTrustRegistrationAccessCodeController.onPageLoad(fakeDraftId).url

        application.stop()
      }

      "not an express, non-taxable trust with non-taxable access code feature enabled" when {

        "agent user" in {

          when(navigator.agentDetailsJourneyUrl(any())).thenReturn("redirect-url")

          forAll(arbitrary[(Boolean, Boolean, Boolean)].suchThat(x => !(x._1 && !x._2 && x._3))) {
            case (isExpress, isTaxable, isFeatureEnabled) =>

              when(mockFeatureFlagService.isNonTaxableAccessCodeEnabled()(any(), any()))
                .thenReturn(Future.successful(isFeatureEnabled))

              val answers = emptyUserAnswers
                .set(ExpressTrustYesNoPage, isExpress).success.value
                .set(TrustTaxableYesNoPage, isTaxable).success.value

              val application = applicationBuilder(userAnswers = Some(answers), affinityGroup = AffinityGroup.Agent)
                .overrides(
                  bind[FeatureFlagService].toInstance(mockFeatureFlagService),
                  bind[TaskListNavigator].toInstance(navigator)
                ).build()

              val request = FakeRequest(POST, beforeYouContinueRoute)

              val result = route(application, request).value

              status(result) mustEqual SEE_OTHER

              redirectLocation(result).value mustEqual "redirect-url"

              application.stop()
          }
        }

        "non-agent user" in {

          forAll(arbitrary[(Boolean, Boolean, Boolean)].suchThat(x => !(x._1 && !x._2 && x._3))) {
            case (isExpress, isTaxable, isFeatureEnabled) =>

              when(mockFeatureFlagService.isNonTaxableAccessCodeEnabled()(any(), any()))
                .thenReturn(Future.successful(isFeatureEnabled))

              val answers = emptyUserAnswers
                .set(ExpressTrustYesNoPage, isExpress).success.value
                .set(TrustTaxableYesNoPage, isTaxable).success.value

              val application = applicationBuilder(userAnswers = Some(answers), affinityGroup = AffinityGroup.Organisation)
                .overrides(bind[FeatureFlagService].toInstance(mockFeatureFlagService))
                .build()

              val request = FakeRequest(POST, beforeYouContinueRoute)

              val result = route(application, request).value

              status(result) mustEqual SEE_OTHER

              redirectLocation(result).value mustEqual controllers.register.routes.TaskListController.onPageLoad(fakeDraftId).url

              application.stop()
          }
        }
      }
    }
  }
}
