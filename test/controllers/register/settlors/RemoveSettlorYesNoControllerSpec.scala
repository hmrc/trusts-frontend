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

package controllers.register.settlors

import base.RegistrationSpecBase
import forms.YesNoFormProvider
import models.core.UserAnswers
import models.core.pages.FullName
import models.core.pages.IndividualOrBusiness._
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito._
import pages.register.settlors.deceased_settlor._
import pages.register.settlors.living_settlor._
import pages.register.settlors.living_settlor.business._
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.settlors.RemoveSettlorYesNoView

import scala.concurrent.Future

class RemoveSettlorYesNoControllerSpec extends RegistrationSpecBase {

  val formProvider = new YesNoFormProvider()
  val prefix: String = "settlors.removeYesNo"
  val form: Form[Boolean] = formProvider.withPrefix(prefix)
  val index: Int = 0

  lazy val removeLivingSettlorYesNoRoute: String = routes.RemoveSettlorYesNoController.onPageLoadLiving(index, fakeDraftId).url
  lazy val removeDeceasedSettlorYesNoRoute: String = routes.RemoveSettlorYesNoController.onPageLoadDeceased(fakeDraftId).url

  val name: FullName = FullName("John", None, "Doe")
  val businessName: String = "Google Ltd"

  val deceasedUserAnswers: UserAnswers = emptyUserAnswers
    .set(SettlorsNamePage, name).success.value
    .set(SettlorDateOfDeathYesNoPage, false).success.value
    .set(SettlorDateOfBirthYesNoPage, false).success.value
    .set(SettlorsNationalInsuranceYesNoPage, true).success.value
    .set(SettlorNationalInsuranceNumberPage, "nino").success.value

  val individualUserAnswers: UserAnswers = emptyUserAnswers
    .set(SettlorIndividualOrBusinessPage(index), Individual).success.value
    .set(SettlorIndividualNamePage(index), name).success.value
    .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
    .set(SettlorIndividualNINOYesNoPage(index), true).success.value
    .set(SettlorIndividualNINOPage(index), "nino").success.value

  val businessUserAnswers: UserAnswers = emptyUserAnswers
    .set(SettlorIndividualOrBusinessPage(index), Business).success.value
    .set(SettlorBusinessNamePage(index), businessName).success.value
    .set(SettlorBusinessUtrYesNoPage(index), true).success.value
    .set(SettlorBusinessUtrPage(index), "utr").success.value

  "RemoveSettlorYesNoController" must {

    "return OK and the correct view for a GET" when {

      "Deceased settlor" in {

        val application = applicationBuilder(userAnswers = Some(deceasedUserAnswers)).build()

        val request = FakeRequest(GET, removeDeceasedSettlorYesNoRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[RemoveSettlorYesNoView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(
            form,
            fakeDraftId,
            routes.RemoveSettlorYesNoController.onSubmitDeceased(fakeDraftId),
            name.toString
          )(request, messages).toString

        application.stop()
      }

      "Living settlor" when {

        "Individual" in {

          val application = applicationBuilder(userAnswers = Some(individualUserAnswers)).build()

          val request = FakeRequest(GET, removeLivingSettlorYesNoRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[RemoveSettlorYesNoView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(
              form,
              fakeDraftId,
              routes.RemoveSettlorYesNoController.onSubmitLiving(index, fakeDraftId),
              name.toString
            )(request, messages).toString

          application.stop()
        }

        "Business" in {

          val application = applicationBuilder(userAnswers = Some(businessUserAnswers)).build()

          val request = FakeRequest(GET, removeLivingSettlorYesNoRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[RemoveSettlorYesNoView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(
              form,
              fakeDraftId,
              routes.RemoveSettlorYesNoController.onSubmitLiving(index, fakeDraftId),
              businessName
            )(request, messages).toString

          application.stop()
        }
      }
    }

    "redirect to add settlors page when NO is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(POST, removeLivingSettlorYesNoRoute)
        .withFormUrlEncodedBody(("value", "false"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.AddASettlorController.onPageLoad(fakeDraftId).url

      application.stop()
    }

    "remove settlor and redirect to add settlors page when YES is submitted" when {

      "deceased settlor" in {

        reset(registrationsRepository)

        when(registrationsRepository.set(any())(any())).thenReturn(Future.successful(true))

        val application = applicationBuilder(userAnswers = Some(deceasedUserAnswers)).build()

        val request = FakeRequest(POST, removeDeceasedSettlorYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.AddASettlorController.onPageLoad(fakeDraftId).url

        val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(registrationsRepository).set(uaCaptor.capture)(any())
        uaCaptor.getValue.get(SettlorsNamePage) mustNot be(defined)
        uaCaptor.getValue.get(SettlorDateOfDeathYesNoPage) mustNot be(defined)
        uaCaptor.getValue.get(SettlorDateOfBirthYesNoPage) mustNot be(defined)
        uaCaptor.getValue.get(SettlorsNationalInsuranceYesNoPage) mustNot be(defined)
        uaCaptor.getValue.get(SettlorNationalInsuranceNumberPage) mustNot be(defined)

        application.stop()
      }

      "living settlor" when {

        "individual" in {

          reset(registrationsRepository)

          when(registrationsRepository.set(any())(any())).thenReturn(Future.successful(true))

          val application = applicationBuilder(userAnswers = Some(individualUserAnswers)).build()

          val request = FakeRequest(POST, removeLivingSettlorYesNoRoute)
            .withFormUrlEncodedBody(("value", "true"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual routes.AddASettlorController.onPageLoad(fakeDraftId).url

          val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(registrationsRepository).set(uaCaptor.capture)(any())
          uaCaptor.getValue.get(SettlorIndividualOrBusinessPage(index)) mustNot be(defined)
          uaCaptor.getValue.get(SettlorIndividualNamePage(index)) mustNot be(defined)
          uaCaptor.getValue.get(SettlorIndividualDateOfBirthYesNoPage(index)) mustNot be(defined)
          uaCaptor.getValue.get(SettlorIndividualNINOYesNoPage(index)) mustNot be(defined)
          uaCaptor.getValue.get(SettlorIndividualNINOPage(index)) mustNot be(defined)

          application.stop()
        }

        "business" in {

          reset(registrationsRepository)

          when(registrationsRepository.set(any())(any())).thenReturn(Future.successful(true))

          val application = applicationBuilder(userAnswers = Some(businessUserAnswers)).build()

          val request = FakeRequest(POST, removeLivingSettlorYesNoRoute)
            .withFormUrlEncodedBody(("value", "true"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual routes.AddASettlorController.onPageLoad(fakeDraftId).url

          val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(registrationsRepository).set(uaCaptor.capture)(any())
          uaCaptor.getValue.get(SettlorIndividualOrBusinessPage(index)) mustNot be(defined)
          uaCaptor.getValue.get(SettlorBusinessNamePage(index)) mustNot be(defined)
          uaCaptor.getValue.get(SettlorBusinessUtrYesNoPage(index)) mustNot be(defined)
          uaCaptor.getValue.get(SettlorBusinessUtrPage(index)) mustNot be(defined)

          application.stop()
        }
      }
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(POST, removeLivingSettlorYesNoRoute)
        .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[RemoveSettlorYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(
          boundForm,
          fakeDraftId,
          routes.RemoveSettlorYesNoController.onSubmitLiving(index, fakeDraftId),
          "the settlor"
        )(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, removeLivingSettlorYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.register.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, removeLivingSettlorYesNoRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.register.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
