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

package controllers.register.agents

import base.RegistrationSpecBase
import controllers.register.routes._
import forms.InternationalAddressFormProvider
import models.NormalMode
import models.core.UserAnswers
import models.core.pages.InternationalAddress
import pages.register.agents.{AgentInternationalAddressPage, AgentNamePage}
import play.api.Application
import play.api.data.Form
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import utils.InputOption
import utils.countryOptions.CountryOptionsNonUK
import views.html.register.agents.AgentInternationalAddressView

import scala.concurrent.Future

class AgentInternationalAddressControllerSpec extends RegistrationSpecBase {

  val formProvider = new InternationalAddressFormProvider()
  val form: Form[InternationalAddress] = formProvider()
  val agencyName = "Hadrian"

  lazy val agentInternationalAddressRoute: String = routes.AgentInternationalAddressController.onPageLoad(NormalMode, fakeDraftId).url

  "AgentInternationalAddress Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers: UserAnswers = emptyUserAnswers.set(AgentNamePage,
        agencyName).success.value

      val application: Application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(GET, agentInternationalAddressRoute)

      val view: AgentInternationalAddressView = application.injector.instanceOf[AgentInternationalAddressView]

      val result: Future[Result] = route(application, request).value

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, countryOptions, NormalMode, fakeDraftId, agencyName)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(AgentInternationalAddressPage, InternationalAddress("line 1", "line 2", Some("line 3"), "country")).success.value
        .set(AgentNamePage, agencyName).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(GET, agentInternationalAddressRoute)

      val view = application.injector.instanceOf[AgentInternationalAddressView]

      val result = route(application, request).value

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(InternationalAddress("line 1", "line 2", Some("line 3"), "country")), countryOptions, NormalMode, fakeDraftId, agencyName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(AgentNamePage,
        agencyName).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent)
          .build()

      val request =
        FakeRequest(POST, agentInternationalAddressRoute)
          .withFormUrlEncodedBody(("line1", "value 1"), ("line2", "value 2"), ("country", "IN"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(AgentNamePage,
        agencyName).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

      val request =
        FakeRequest(POST, agentInternationalAddressRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[AgentInternationalAddressView]

      val result = route(application, request).value

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, countryOptions, NormalMode, fakeDraftId, agencyName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, AffinityGroup.Agent).build()

      val request = FakeRequest(GET, agentInternationalAddressRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, AffinityGroup.Agent).build()

      val request =
        FakeRequest(POST, agentInternationalAddressRoute)
          .withFormUrlEncodedBody(("field1", "value 1"), ("field2", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to AgentNamePage when agency name is not answered" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(GET, agentInternationalAddressRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.AgentNameController.onPageLoad(NormalMode, fakeDraftId).url

      application.stop()
    }

    "redirect to unauthorised page when accessing Agent resources with AffinityGroup.Organisation" in {

      val application = applicationBuilder(userAnswers = None, AffinityGroup.Organisation).build()

      val request = FakeRequest(GET, agentInternationalAddressRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual UnauthorisedController.onPageLoad().url

      application.stop()
    }
  }
}
