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

package views.register

import java.time.{LocalDate, LocalDateTime}

import models.core.pages.{FullName, IndividualOrBusiness, UKAddress}
import models.registration.pages.Status.Completed
import models.registration.pages.TrusteesBasedInTheUK.UKBasedTrustees
import models.registration.pages._
import pages.entitystatus._
import pages.register.settlors.SetUpAfterSettlorDiedYesNoPage
import pages.register.settlors.deceased_settlor._
import pages.register.trust_details._
import pages.register.{RegistrationSubmissionDatePage, RegistrationTRNPage}
import uk.gov.hmrc.http.HeaderCarrier
import utils.AccessibilityHelper._
import utils.print.register.PrintUserAnswersHelper
import utils.{DateFormatter, TestUserAnswers}
import views.behaviours.ViewBehaviours
import views.html.register.ConfirmationAnswerPageView

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ConfirmationAnswerPageViewSpec extends ViewBehaviours {
  val index = 0

  "ConfirmationAnswerPage view" must {

    val userAnswers =
      TestUserAnswers.emptyUserAnswers
        .set(TrustNamePage, "New Trust").success.value
        .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
        .set(GovernedInsideTheUKPage, true).success.value
        .set(AdministrationInsideUKPage, true).success.value
        .set(TrusteesBasedInTheUKPage, UKBasedTrustees).success.value
        .set(EstablishedUnderScotsLawPage, true).success.value
        .set(TrustResidentOffshorePage, false).success.value
        .set(TrustDetailsStatus, Completed).success.value

        .set(SetUpAfterSettlorDiedYesNoPage, true).success.value
        .set(SettlorsNamePage, FullName("First", None, "Last")).success.value
        .set(SettlorDateOfDeathYesNoPage, true).success.value
        .set(SettlorDateOfDeathPage, LocalDate.of(2010, 10, 10)).success.value
        .set(SettlorDateOfBirthYesNoPage, true).success.value
        .set(SettlorsDateOfBirthPage, LocalDate.of(2010, 10, 10)).success.value
        .set(SettlorsNationalInsuranceYesNoPage, true).success.value
        .set(SettlorNationalInsuranceNumberPage, "AB123456C").success.value
        .set(SettlorsLastKnownAddressYesNoPage, true).success.value
        .set(WasSettlorsAddressUKYesNoPage, true).success.value
        .set(SettlorsUKAddressPage, UKAddress("Line1", "Line2", None, None, "NE1 1ZZ")).success.value
        .set(DeceasedSettlorStatus, Status.Completed).success.value

        .set(RegistrationTRNPage, "XNTRN000000001").success.value
        .set(RegistrationSubmissionDatePage, LocalDateTime.of(2010, 10, 10, 13, 10, 10)).success.value

    val formatter = injector.instanceOf[DateFormatter]

    val trnDateTime: String = formatter.formatDate(LocalDateTime.of(2010, 10, 10, 13, 10, 10))
    val name = "First Last"
    val trusteeName = "TrusteeFirst TrusteeLast"
    val yes = "Yes"
    val no = "No"

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val app = applicationBuilder().build()

    val helper = app.injector.instanceOf[PrintUserAnswersHelper]

    val viewFuture = helper.summary(fakeDraftId, userAnswers).map {
      sections =>
        val view = viewFor[ConfirmationAnswerPageView](Some(userAnswers))

        view.apply(sections, formatReferenceNumber("XNTRN000000001"), trnDateTime)(fakeRequest, messages)
    }

    val view = Await.result(viewFuture, Duration.Inf)

    val doc = asDocument(view)

    behave like normalPage(view, None, "confirmationAnswerPage")

    "assert header content" in {
      assertContainsText(doc, messages("confirmationAnswerPage.paragraph1", formatReferenceNumber("XNTRN000000001")))
      assertContainsText(doc, messages("confirmationAnswerPage.paragraph2", trnDateTime))
    }

    "assert correct number of headers and subheaders" in {
      val wrapper = doc.getElementById("wrapper")
      val headers = wrapper.getElementsByTag("h2")
      val subHeaders = wrapper.getElementsByTag("h3")

      headers.size mustBe 2
      subHeaders.size mustBe 0
    }

    "assert question labels for Trust details" in {
      assertContainsQuestionAnswerPair(doc, messages("trustName.checkYourAnswersLabel"), "New Trust")
      assertContainsQuestionAnswerPair(doc, messages("whenTrustSetup.checkYourAnswersLabel"), "10 October 2010")
    }

    "assert question labels for Settlors" in {
        assertContainsQuestionAnswerPair(doc, messages("setUpAfterSettlorDied.checkYourAnswersLabel"), yes)
        assertContainsQuestionAnswerPair(doc, messages("settlorsName.checkYourAnswersLabel"), name)
        assertContainsQuestionAnswerPair(doc, messages("settlorDateOfBirthYesNo.checkYourAnswersLabel", name), yes)
        assertContainsQuestionAnswerPair(doc, messages("settlorsDateOfBirth.checkYourAnswersLabel", name), "10 October 2010")
        assertContainsQuestionAnswerPair(doc, messages("settlorDateOfDeathYesNo.checkYourAnswersLabel", name), yes)
        assertContainsQuestionAnswerPair(doc, messages("settlorDateOfDeath.checkYourAnswersLabel", name), "10 October 2010")
        assertContainsQuestionAnswerPair(doc, messages("settlorsNationalInsuranceYesNo.checkYourAnswersLabel", name), yes)
        assertContainsQuestionAnswerPair(doc, messages("settlorNationalInsuranceNumber.checkYourAnswersLabel", name), "AB 12 34 56 C")
        assertContainsQuestionAnswerPair(doc, messages("settlorsLastKnownAddressYesNo.checkYourAnswersLabel", name), yes)
        assertContainsQuestionAnswerPair(doc, messages("wasSettlorsAddressUKYesNo.checkYourAnswersLabel", name), yes)
        assertContainsQuestionAnswerPair(doc, messages("settlorsUKAddress.checkYourAnswersLabel", name), "Line1 Line2 NE1 1ZZ")
    }
  }
}
