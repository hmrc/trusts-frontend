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

package controllers.playback

import base.PlaybackSpecBase
import connector.{TrustClaim, TrustConnector, TrustsStoreConnector}
import mapping.playback.{FakeFailingUserAnswerExtractor, FakeUserAnswerExtractor, UserAnswersExtractor}
import models.playback.http._
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import pages.playback.WhatIsTheUTRVariationPage
import play.api.Application
import play.api.inject.bind
import play.api.libs.json._
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.PlaybackRepository
import services.{FakeFailingPlaybackAuthenticationService, FakePlaybackAuthenticationService, PlaybackAuthenticationService}
import uk.gov.hmrc.auth.core.AffinityGroup
import views.html.playback.status._

import scala.concurrent.Future
import scala.io.Source

class TrustStatusControllerSpec extends PlaybackSpecBase with BeforeAndAfterEach {

  trait LocalSetup {

    def utr = "1234567890"

    def userAnswers = emptyUserAnswers.set(WhatIsTheUTRVariationPage, utr).success.value

    val fakeTrustConnector: TrustConnector = mock[TrustConnector]
    val fakeTrustStoreConnector: TrustsStoreConnector = mock[TrustsStoreConnector]
    val fakePlaybackRepository: PlaybackRepository = mock[PlaybackRepository]

    val application: Application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(
      bind[TrustConnector].to(fakeTrustConnector),
      bind[TrustsStoreConnector].to(fakeTrustStoreConnector),
      bind[PlaybackAuthenticationService].to(new FakePlaybackAuthenticationService()),
      bind[UserAnswersExtractor].to[FakeUserAnswerExtractor]
    ).build()

    def request: FakeRequest[AnyContentAsEmpty.type]

    def result: Future[Result] = route(application, request).value
  }

  "TrustStatus Controller" when {

    "must return OK and the correct view for GET ../status/closed" in new LocalSetup {

      override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.closed().url)

      val view: ClosedErrorView = application.injector.instanceOf[ClosedErrorView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(AffinityGroup.Individual, utr)(fakeRequest, messages).toString

      application.stop()
    }

    "must return OK and the correct view for GET ../status/processing" in new LocalSetup {

      override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.processing().url)

      val view: StillProcessingErrorView = application.injector.instanceOf[StillProcessingErrorView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(AffinityGroup.Individual, utr)(fakeRequest, messages).toString

      application.stop()
    }

    "must return OK and the correct view for GET ../status/not-found" in new LocalSetup {

      override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.notFound().url)

      val view: DoesNotMatchErrorView = application.injector.instanceOf[DoesNotMatchErrorView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(AffinityGroup.Individual)(fakeRequest, messages).toString

      application.stop()
    }

    "must return OK and the correct view for GET ../status/locked" in new LocalSetup {

      override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.locked().url)

      val view: TrustLockedView = application.injector.instanceOf[TrustLockedView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(utr)(fakeRequest, messages).toString

      application.stop()
    }

    "must return OK and the correct view for GET ../status/already-claimed" in new LocalSetup {

      override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.alreadyClaimed().url)

      val view: TrustAlreadyClaimedView = application.injector.instanceOf[TrustAlreadyClaimedView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(utr)(fakeRequest, messages).toString

      application.stop()
    }

    "must return OK and the correct view for GET ../status/sorry-there-has-been-a-problem" in new LocalSetup {

      override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.sorryThereHasBeenAProblem().url)

      val view: PlaybackProblemContactHMRCView = application.injector.instanceOf[PlaybackProblemContactHMRCView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(utr)(fakeRequest, messages).toString

      application.stop()
    }

    "must return SERVICE_UNAVAILABLE and the correct view for GET ../status/down" in new LocalSetup {

      override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.down().url)

      val view: IVDownView = application.injector.instanceOf[IVDownView]

      status(result) mustEqual SERVICE_UNAVAILABLE

      contentAsString(result) mustEqual
        view(AffinityGroup.Individual)(fakeRequest, messages).toString

      application.stop()
    }

    "must redirect to the correct route for GET ../status" when {

      "a Closed status is received from the trust connector" in new LocalSetup {

        override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.status().url)

        when(fakeTrustStoreConnector.get(any[String], any[String])(any(), any()))
          .thenReturn(Future.successful(Some(TrustClaim("1234567890", trustLocked = false, managedByAgent = false))))

        when(fakeTrustConnector.playback(any[String])(any(), any())).thenReturn(Future.successful(Closed))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/maintain-a-trust/status/closed"

        application.stop()
      }

      "a Processing status is received from the trust connector" in new LocalSetup {

        override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.status().url)

        when(fakeTrustStoreConnector.get(any[String], any[String])(any(), any()))
          .thenReturn(Future.successful(Some(TrustClaim("1234567890", trustLocked = false, managedByAgent = false))))

        when(fakeTrustConnector.playback(any[String])(any(), any())).thenReturn(Future.successful(Processing))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/maintain-a-trust/status/processing"

        application.stop()
      }

      "a NotFound status is received from the trust connector" in new LocalSetup {

        override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.status().url)

        when(fakeTrustStoreConnector.get(any[String], any[String])(any(), any()))
          .thenReturn(Future.successful(Some(TrustClaim("1234567890", trustLocked = false, managedByAgent = false))))

        when(fakeTrustConnector.playback(any[String])(any(), any())).thenReturn(Future.successful(UtrNotFound))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/maintain-a-trust/status/not-found"

        application.stop()
      }

      "A locked trust claim is returned from the trusts store connector" in new LocalSetup {

        override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.status().url)

        when(fakeTrustStoreConnector.get(any[String], any[String])(any(), any()))
          .thenReturn(Future.successful(Some(TrustClaim("1234567890", trustLocked = true, managedByAgent = false))))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/maintain-a-trust/status/locked"

        application.stop()
      }

      "a ServiceUnavailable status is received from the trust connector" in new LocalSetup {

        override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.status().url)

        when(fakeTrustStoreConnector.get(any[String], any[String])(any(), any()))
          .thenReturn(Future.successful(Some(TrustClaim("1234567890", trustLocked = false, managedByAgent = false))))

        when(fakeTrustConnector.playback(any[String])(any(), any())).thenReturn(Future.successful(TrustServiceUnavailable))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/maintain-a-trust/status/down"

        application.stop()
      }

      "a Processed status is received from the trust connector" when {

        "user is authenticated for playback" when {

          "user answers is extracted" must {

            "redirect to maintain a trust" in new LocalSetup {

              override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.status().url)

              val payload : String =
                Source.fromFile(getClass.getResource("/display-trust.json").getPath).mkString

              val json : JsValue = Json.parse(payload)

              val getTrust = json.as[GetTrustDesResponse].getTrust.value

              when(fakeTrustStoreConnector.get(any[String], any[String])(any(), any()))
                .thenReturn(Future.successful(Some(TrustClaim("1234567890", trustLocked = false, managedByAgent = false))))

              when(fakeTrustConnector.playback(any[String])(any(), any()))
                .thenReturn(Future.successful(Processed(getTrust, "9873459837459837")))

              when(fakePlaybackRepository.set(any())).thenReturn(Future.successful(true))

              status(result) mustEqual SEE_OTHER

              redirectLocation(result).value mustEqual routes.InformationMaintainingThisTrustController.onPageLoad().url

              application.stop()
            }

          }

          "user answers is not extracted" must {

            "render sorry there's been a problem" in {

              lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.status().url)

              val fakeTrustConnector: TrustConnector = mock[TrustConnector]
              val fakeTrustStoreConnector: TrustsStoreConnector = mock[TrustsStoreConnector]

              val userAnswers = emptyUserAnswers.set(WhatIsTheUTRVariationPage, "1234567890").success.value

              def application: Application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(
                bind[TrustConnector].to(fakeTrustConnector),
                bind[TrustsStoreConnector].to(fakeTrustStoreConnector),
                bind[UserAnswersExtractor].to[FakeFailingUserAnswerExtractor],
                bind[PlaybackAuthenticationService].to(new FakePlaybackAuthenticationService())
              ).build()

              val payload : String =
                Source.fromFile(getClass.getResource("/display-trust.json").getPath).mkString

              val json : JsValue = Json.parse(payload)

              val getTrust = json.as[GetTrustDesResponse].getTrust.value

              when(fakeTrustStoreConnector.get(any[String], any[String])(any(), any()))
                .thenReturn(Future.successful(Some(TrustClaim("1234567890", trustLocked = false, managedByAgent = false))))

              when(fakeTrustConnector.playback(any[String])(any(), any()))
                .thenReturn(Future.successful(Processed(getTrust, "9873459837459837")))

              when(playbackRepository.set(any())).thenReturn(Future.successful(true))

              val result: Future[Result] = route(application, request).value

              status(result) mustEqual SEE_OTHER

              redirectLocation(result).value mustEqual controllers.playback.routes.TrustStatusController.sorryThereHasBeenAProblem().url

              application.stop()
            }

          }

        }

        "user is not authenticated for playback" must {

          "render authentication reason page" in {

            lazy val request = FakeRequest(GET, routes.TrustStatusController.status().url)

            def utr = "1234567890"

            def userAnswers = emptyUserAnswers.set(WhatIsTheUTRVariationPage, utr).success.value

            val fakeTrustConnector: TrustConnector = mock[TrustConnector]
            val fakeTrustStoreConnector: TrustsStoreConnector = mock[TrustsStoreConnector]

            def application: Application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(
              bind[TrustConnector].to(fakeTrustConnector),
              bind[TrustsStoreConnector].to(fakeTrustStoreConnector),
              bind[PlaybackAuthenticationService].to(new FakeFailingPlaybackAuthenticationService())
            ).build()

            val payload : String =
              Source.fromFile(getClass.getResource("/display-trust.json").getPath).mkString

            val json : JsValue = Json.parse(payload)

            val getTrust = json.as[GetTrustDesResponse].getTrust.value

            def result: Future[Result] = route(application, request).value

            when(fakeTrustStoreConnector.get(any[String], any[String])(any(), any()))
              .thenReturn(Future.successful(Some(TrustClaim("1234567890", trustLocked = false, managedByAgent = false))))

            when(fakeTrustConnector.playback(any[String])(any(), any()))
              .thenReturn(Future.successful(Processed(getTrust, "9873459837459837")))

            when(playbackRepository.set(any())).thenReturn(Future.successful(true))

            status(result) mustEqual UNAUTHORIZED

            application.stop()
          }

        }

      }
    }
  }
}
