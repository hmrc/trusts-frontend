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

package utils.print.playback


import java.time.LocalDate

import base.PlaybackSpecBase
import models.core.pages.{FullName, IndividualOrBusiness, InternationalAddress, UKAddress}
import models.registration.pages.PassportOrIdCardDetails
import pages.register.trust_details.{CorrespondenceAddressInTheUKPage, CorrespondenceAddressPage}
import pages.register.trustees._
import pages.register.trustees.individual._
import pages.register.trustees.organisation._
import play.twirl.api.Html

class TrusteesPrintPlaybackHelperSpec extends PlaybackSpecBase with AnswerSectionMatchers with UserAnswersWriting {

  "when the lead trustee is an UK individual" must {
    "generate lead trustee section" in {
      val helper = injector.instanceOf[PrintPlaybackHelper]

      val (answers, _) = (for {
        _ <- individualUKTrustee(0)
        _ <- TrusteesNamePage(0) is FullName("Wild", Some("Bill"), "Hickock")
        _ <- TrusteesDateOfBirthPage(0) is LocalDate.parse("1975-01-23")
        _ <- TrusteesNinoPage(0) is "AA111111A"
        _ <- TrusteeAddressYesNoPage(0).isRemoved
        _ <- TrusteeAddressInTheUKPage(0) is true
        _ <- TrusteesUkAddressPage(0) is UKAddress("Address 1", "Address 2", None, None, "AA11 1AA")
        _ <- TelephoneNumberPage(0) is "67676767676"
        _ <- TrusteeEmailYesNoPage(0) is true
        _ <- EmailPage(0) is "aa@aabb.com"
        _ <- IsThisLeadTrusteePage(0) is true
      } yield Unit).run(emptyUserAnswers).value

      val result = helper.entities(answers)

      result must containHeadingSection(messages("answerPage.section.trustees.heading"))
      result must containSectionWithHeadingAndValues(messages("answerPage.section.leadTrustee.subheading"),
        "What is the lead trustee’s name?" -> Html("Wild Bill Hickock"),
        "What is Wild Bill Hickock’s date of birth?" -> Html("23 January 1975"),
        "Is Wild Bill Hickock a UK citizen?"-> Html("Yes"),
        "What is Wild Bill Hickock’s National Insurance number?" -> Html("AA 11 11 11 A"),
        "Does Wild Bill Hickock live in the UK?" -> Html("Yes"),
        "What is Wild Bill Hickock’s address?" -> Html("Address 1<br />Address 2<br />AA11 1AA"),
        "Do you know Wild Bill Hickock’s email address?" -> Html("Yes"),
        "What is Wild Bill Hickock’s email address?" -> Html("aa@aabb.com"),
        "What is Wild Bill Hickock’s telephone number?" -> Html("67676767676")
      )
    }
  }

  "when the lead trustee is a non-UK individual" must {
    "generate a lead trustee section" in {
      val helper = injector.instanceOf[PrintPlaybackHelper]

      val (answers, _) = (for {
        _ <- individualUKTrustee(0)
        _ <- individualNonUkTrustee(0)
        _ <- TrusteesNamePage(0) is FullName("William", None, "Bonny")
        _ <- TrusteesDateOfBirthPage(0) is LocalDate.parse("1975-01-23")
        _ <- TrusteeAddressYesNoPage(0).isRemoved
        _ <- TrusteesInternationalAddressPage(0) is InternationalAddress("Address 1", "Address 2", None, "DE")
        _ <- TelephoneNumberPage(0) is "67676767676"
        _ <- TrusteeEmailYesNoPage(0) is true
        _ <- EmailPage(0) is "aa@aabb.com"
        _ <- IsThisLeadTrusteePage(0) is true
        _ <- TrusteePassportIDCardPage(0) is PassportOrIdCardDetails("DE", "KSJDFKSDHF6456545147852369QWER", LocalDate.of(2020,2,2))
      } yield Unit).run(emptyUserAnswers).value

      val result = helper.entities(answers)

      result must containHeadingSection(messages("answerPage.section.trustees.heading"))
      result must containSectionWithHeadingAndValues(messages("answerPage.section.leadTrustee.subheading"),
      "What is the lead trustee’s name?" -> Html("William Bonny"),
      "What is William Bonny’s date of birth?" -> Html("23 January 1975"),
      "Is William Bonny a UK citizen?"-> Html("No"),
      "Does William Bonny live in the UK?" -> Html("No"),
      "What is William Bonny’s address?" -> Html("Address 1<br />Address 2<br />Germany"),
      "What are William Bonny’s passport or ID card details?"-> Html("Germany<br />KSJDFKSDHF6456545147852369QWER<br />2 February 2020"),
      "Do you know William Bonny’s email address?" -> Html("Yes"),
      "What is William Bonny’s email address?" -> Html("aa@aabb.com"),
      "What is William Bonny’s telephone number?" -> Html("67676767676")
      )
    }
  }

  "when the lead trustee is an UK individual with nino and no address" must {
    "generate lead trustee section with correspondence address" in {
      val helper = injector.instanceOf[PrintPlaybackHelper]

      val (answers, _) = (for {
        _ <- individualUKTrustee(0)
        _ <- TrusteesNamePage(0) is FullName("Wild", Some("Bill"), "Hickock")
        _ <- TrusteesDateOfBirthPage(0) is LocalDate.parse("1975-01-23")
        _ <- TrusteesNinoPage(0) is "AA111111A"
        _ <- TrusteeAddressYesNoPage(0).isRemoved
        _ <- TrusteeAddressInTheUKPage(0).isRemoved
        _ <- TrusteesUkAddressPage(0).isRemoved
        _ <- CorrespondenceAddressInTheUKPage is true
        _ <- CorrespondenceAddressPage is UKAddress("Address 1", "Address 2", None, None, "AA11 1AA")
        _ <- TelephoneNumberPage(0) is "67676767676"
        _ <- TrusteeEmailYesNoPage(0) is true
        _ <- EmailPage(0) is "aa@aabb.com"
        _ <- IsThisLeadTrusteePage(0) is true
      } yield Unit).run(emptyUserAnswers).value

      val result = helper.entities(answers)

      result must containHeadingSection(messages("answerPage.section.trustees.heading"))
      result must containSectionWithHeadingAndValues(messages("answerPage.section.leadTrustee.subheading"),
        "What is the lead trustee’s name?" -> Html("Wild Bill Hickock"),
        "What is Wild Bill Hickock’s date of birth?" -> Html("23 January 1975"),
        "Is Wild Bill Hickock a UK citizen?"-> Html("Yes"),
        "What is Wild Bill Hickock’s National Insurance number?" -> Html("AA 11 11 11 A"),
        "Does Wild Bill Hickock live in the UK?" -> Html("Yes"),
        "What is Wild Bill Hickock’s address?" -> Html("Address 1<br />Address 2<br />AA11 1AA"),
        "Do you know Wild Bill Hickock’s email address?" -> Html("Yes"),
        "What is Wild Bill Hickock’s email address?" -> Html("aa@aabb.com"),
        "What is Wild Bill Hickock’s telephone number?" -> Html("67676767676")
      )
    }
  }

  "when the lead trustee is a company" must {
    "generate a lead trustee section" in {
      val helper = injector.instanceOf[PrintPlaybackHelper]

      val (answers, _) = (for {
        _ <- ukCompanyTrustee(0)
        _ <- TrusteeOrgNamePage(0) is "Lead Trustee Company"
        _ <- TrusteeUtrYesNoPage(0) is true
        _ <- TrusteesUtrPage(0) is "1234567890"
        _ <- TrusteeAddressYesNoPage(0).isRemoved
        _ <- TrusteeAddressInTheUKPage(0) is true
        _ <- TrusteesUkAddressPage(0) is UKAddress("Address 1", "Address 2", None, None, "AA11 1AA")
        _ <- TrusteeEmailYesNoPage(0) is false
        _ <- TelephoneNumberPage(0) is "67676767676"
        _ <- IsThisLeadTrusteePage(0) is true
      } yield Unit).run(emptyUserAnswers).value

      val result = helper.entities(answers)

      result must containHeadingSection(messages("answerPage.section.trustees.heading"))
      result must containSectionWithHeadingAndValues(messages("answerPage.section.leadTrustee.subheading"),
        "What is the business’s name?" -> Html("Lead Trustee Company"),
        "Is this trustee a UK registered company?"-> Html("Yes"),
        "What is Lead Trustee Company Unique Taxpayer Reference (UTR) number?" -> Html("1234567890"),
        "Is Lead Trustee Company’s address in the UK?" -> Html("Yes"),
        "What is Lead Trustee Company’s address?" -> Html("Address 1<br />Address 2<br />AA11 1AA"),
        "Do you know Lead Trustee Company’s email address?" -> Html("No"),
        "What is Lead Trustee Company’s telephone number?" -> Html("67676767676")
      )
    }
  }

  "when the lead trustee is a company with utr and no address" must {
    "generate a lead trustee section with correspondence address" in {
      val helper = injector.instanceOf[PrintPlaybackHelper]

      val (answers, _) = (for {
        _ <- ukCompanyTrustee(0)
        _ <- TrusteeOrgNamePage(0) is "Lead Trustee Company"
        _ <- TrusteeUtrYesNoPage(0) is true
        _ <- TrusteesUtrPage(0) is "1234567890"
        _ <- TrusteeAddressYesNoPage(0).isRemoved
        _ <- TrusteeAddressInTheUKPage(0).isRemoved
        _ <- TrusteesUkAddressPage(0).isRemoved
        _ <- CorrespondenceAddressInTheUKPage is true
        _ <- CorrespondenceAddressPage is UKAddress("Address 1", "Address 2", None, None, "AA11 1AA")
        _ <- TrusteeEmailYesNoPage(0) is true
        _ <- EmailPage(0) is "aa@aabb.com"
        _ <- TelephoneNumberPage(0) is "67676767676"
        _ <- IsThisLeadTrusteePage(0) is true
      } yield Unit).run(emptyUserAnswers).value

      val result = helper.entities(answers)

      result must containHeadingSection(messages("answerPage.section.trustees.heading"))
      result must containSectionWithHeadingAndValues(messages("answerPage.section.leadTrustee.subheading"),
        "What is the business’s name?" -> Html("Lead Trustee Company"),
        "Is this trustee a UK registered company?"-> Html("Yes"),
        "What is Lead Trustee Company Unique Taxpayer Reference (UTR) number?" -> Html("1234567890"),
        "Is Lead Trustee Company’s address in the UK?" -> Html("Yes"),
        "What is Lead Trustee Company’s address?" -> Html("Address 1<br />Address 2<br />AA11 1AA"),
        "Do you know Lead Trustee Company’s email address?" -> Html("Yes"),
        "What is Lead Trustee Company’s email address?" -> Html("aa@aabb.com"),
        "What is Lead Trustee Company’s telephone number?" -> Html("67676767676")
      )
    }
  }

  "when the lead trustee is a company and other trustees" must {
    "generate a trustee section for each trustee" in {
      val helper = injector.instanceOf[PrintPlaybackHelper]

      val (answers, _) = (for {
        _ <- IsThisLeadTrusteePage(0) is true
        _ <- TrusteeIndividualOrBusinessPage(0) is IndividualOrBusiness.Business
        _ <- TrusteeUtrYesNoPage(0) is true
        _ <- TrusteeOrgNamePage(0) is "Lead Trustee Company"
        _ <- TrusteesUtrPage(0) is "1234567890"
        _ <- TrusteeOrgAddressUkYesNoPage(0) is true
        _ <- TrusteeOrgAddressUkPage(0) is UKAddress("Line 1", "Line 2", None, None, "AB1 1AB")
        _ <- TelephoneNumberPage(0) is "0191 1111111"

        _ <- IsThisLeadTrusteePage(1) is false
        _ <- TrusteeIndividualOrBusinessPage(1) is IndividualOrBusiness.Business
        _ <- TrusteeOrgNamePage(1) is "Trustee Company"
        _ <- TrusteeUtrYesNoPage(1) is false
        _ <- TrusteeOrgAddressUkYesNoPage(1) is false
        _ <- TrusteeOrgAddressInternationalPage(1) is InternationalAddress("Line 1", "Line 2", None, "DE")

        _ <- IsThisLeadTrusteePage(2) is false
        _ <- TrusteeIndividualOrBusinessPage(2) is IndividualOrBusiness.Individual
        _ <- TrusteesNamePage(2) is FullName("Individual", None, "Trustee")
        _ <- TrusteeDateOfBirthYesNoPage(2) is true
        _ <- TrusteesDateOfBirthPage(2) is LocalDate.of(1975, 1, 23)
        _ <- TrusteeNinoYesNoPage(2) is true
        _ <- TrusteesNinoPage(2) is "AA000000A"
      } yield Unit).run(emptyUserAnswers).value

      val result = helper.entities(answers)

      result must containHeadingSection(messages("answerPage.section.trustees.heading"))
      result must containSectionWithHeadingAndValues(messages("answerPage.section.leadTrustee.subheading"),
        "Is this trustee a UK registered company?"-> Html("Yes"),
        "What is the business’s name?" -> Html("Lead Trustee Company"),
        "What is Lead Trustee Company Unique Taxpayer Reference (UTR) number?" -> Html("1234567890"),
        "Is Lead Trustee Company’s address in the UK?" -> Html("Yes"),
        "What is Lead Trustee Company’s address?" -> Html("Line 1<br />Line 2<br />AB1 1AB"),
        "What is Lead Trustee Company’s telephone number?" -> Html("0191 1111111")
      )

      result must containSectionWithHeadingAndValues(messages("answerPage.section.trustee.subheading") + " 2",
        "What is the business’s name?" -> Html("Trustee Company"),
        "Do you know Trustee Company’s Unique Taxpayer Reference (UTR) number?"-> Html("No"),
        "Is Trustee Company’s address in the UK?" -> Html("No"),
        "What is Trustee Company’s address?" -> Html("Line 1<br />Line 2<br />Germany")
      )

      result must containSectionWithHeadingAndValues(messages("answerPage.section.trustee.subheading") + " 3",
        "What is the trustee’s name?" -> Html("Individual Trustee"),
        "Do you know Individual Trustee’s date of birth?"-> Html("Yes"),
        "What is Individual Trustee’s date of birth?" -> Html("23 January 1975"),
        "Do you know Individual Trustee’s National Insurance number?"-> Html("Yes"),
        "What is Individual Trustee’s National Insurance number?" -> Html("AA 00 00 00 A")
      )

    }
  }


}
