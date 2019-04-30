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

import java.time.{LocalDate, ZoneOffset}

import base.SpecBase
import models.{FullName, IndividualOrBusiness, UKAddress, UserAnswers}
import pages._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.IndividualBenficiaryAnswersView

class IndividualBeneficiaryAnswersControllerSpec extends SpecBase {

  val index = 0

  "IndividualBeneficiaryAnswers Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers =
        UserAnswers(userAnswersId)
          .set(IndividualBeneficiaryNamePage(index), FullName("first name", None, "last name")).success.value
          .set(IndividualBeneficiaryDateOfBirthYesNoPage(index),true).success.value
          .set(IndividualBeneficiaryDateOfBirthPage(index),LocalDate.now(ZoneOffset.UTC)).success.value
          .set(IndividualBeneficiaryIncomeYesNoPage(index),true).success.value

      val countryOptions = injector.instanceOf[CountryOptions]
      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers)

      val expectedSections = Seq(
        AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.individualBeneficiaryName(index).value,
            checkYourAnswersHelper.individualBeneficiaryDateOfBirthYesNo(index).value,
            checkYourAnswersHelper.individualBeneficiaryDateOfBirth(index).value,
            checkYourAnswersHelper.individualBeneficiaryIncomeYesNo(index).value
          )
        )
      )

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, routes.IndividualBeneficiaryAnswersController.onPageLoad(index).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[IndividualBenficiaryAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(index, expectedSections)(fakeRequest, messages).toString

      application.stop()
    }
  }
}
