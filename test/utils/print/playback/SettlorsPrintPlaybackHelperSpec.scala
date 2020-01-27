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
import models.playback.UserAnswers
import models.registration.pages.KindOfBusiness.Trading
import models.registration.pages.PassportOrIdCardDetails
import pages.register.settlors.deceased_settlor._
import pages.register.settlors.living_settlor
import play.api.libs.json.Writes
import play.twirl.api.Html
import queries.Settable
import viewmodels.{AnswerRow, AnswerSection}

class SettlorsPrintPlaybackHelperSpec extends PlaybackSpecBase {

  "Settlors print playback helper" must {

    "generate deceased settlor sections for maximum dataset" in {

      val name = "Adam Smith"

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = emptyUserAnswers
        .set(SettlorsNamePage, FullName("Adam", None, "Smith")).success.value
        .set(SettlorDateOfDeathYesNoPage, true).success.value
        .set(SettlorDateOfDeathPage, LocalDate.of(2010, 10, 10)).success.value
        .set(SettlorDateOfBirthYesNoPage, true).success.value
        .set(SettlorsDateOfBirthPage, LocalDate.of(1991, 8, 27)).success.value
        .set(SettlorsNationalInsuranceYesNoPage, true).success.value
        .set(SettlorNationalInsuranceNumberPage, "JP121212A").success.value

      val result = helper.entities(answers)

      result mustBe Seq(
        AnswerSection(None, Nil, Some("answerPage.section.deceasedSettlor.heading")),
        AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = messages("settlorsName.checkYourAnswersLabel"), answer = Html("Adam Smith"), changeUrl = None, canEdit = false),
            AnswerRow(label = messages("settlorDateOfDeathYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = None, canEdit = false),
            AnswerRow(label = messages("settlorDateOfDeath.checkYourAnswersLabel", name), answer = Html("10 October 2010"), changeUrl = None, canEdit = false),
            AnswerRow(label = messages("settlorDateOfBirthYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = None, canEdit = false),
            AnswerRow(label = messages("settlorsDateOfBirth.checkYourAnswersLabel", name), answer = Html("27 August 1991"), changeUrl = None, canEdit = false),
            AnswerRow(label = messages("settlorsNationalInsuranceYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = None, canEdit = false),
            AnswerRow(label = messages("settlorNationalInsuranceNumber.checkYourAnswersLabel", name), answer = Html("JP 12 12 12 A"), changeUrl = None, canEdit = false)
          ),
          sectionKey = None
        )
      )

    }

    "generate deceased settlor sections for minimum dataset" in {

      val name = "Adam Smith"

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = emptyUserAnswers
        .set(SettlorsNamePage, FullName("Adam", None, "Smith")).success.value
        .set(SettlorDateOfDeathYesNoPage, false).success.value
        .set(SettlorDateOfBirthYesNoPage, false).success.value
        .set(SettlorsNationalInsuranceYesNoPage, false).success.value
        .set(SettlorsLastKnownAddressYesNoPage, true).success.value
        .set(WasSettlorsAddressUKYesNoPage, true).success.value
        .set(SettlorsUKAddressPage, UKAddress(
          line1 = "line 1",
          line2 = "line 2",
          line3 = Some("line 3"),
          line4 = Some("line 4"),
          postcode = "NE981ZZ"
        )).success.value
        .set(SettlorsPassportIDCardPage,
          PassportOrIdCardDetails("DE", "123456789", LocalDate.of(2021,10,10))
        ).success.value

      val result = helper.entities(answers)

      result mustBe Seq(
        AnswerSection(None, Nil, Some("answerPage.section.deceasedSettlor.heading")),
        AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = messages("settlorsName.checkYourAnswersLabel"), answer = Html("Adam Smith"), changeUrl = None, canEdit = false),
            AnswerRow(label = messages("settlorDateOfDeathYesNo.checkYourAnswersLabel", name), answer = Html("No"), changeUrl = None, canEdit = false),
            AnswerRow(label = messages("settlorDateOfBirthYesNo.checkYourAnswersLabel", name), answer = Html("No"), changeUrl = None, canEdit = false),
            AnswerRow(label = messages("settlorsNationalInsuranceYesNo.checkYourAnswersLabel", name), answer = Html("No"), changeUrl = None, canEdit = false),
            AnswerRow(label = messages("settlorsLastKnownAddressYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = None, canEdit = false),
            AnswerRow(label = messages("wasSettlorsAddressUKYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = None, canEdit = false),
            AnswerRow(label = messages("settlorsUKAddress.checkYourAnswersLabel", name), answer = Html("line 1<br />line 2<br />line 3<br />line 4<br />NE981ZZ"), changeUrl = None, canEdit = false),
            AnswerRow(label = messages("settlorsPassportOrIdCard.checkYourAnswersLabel", name), answer = Html("Germany<br />123456789<br />10 October 2021"), changeUrl = None, canEdit = false)
          ),
          sectionKey = None
        )
      )
    }

    def uaSet[T:Writes](settable: Settable[T], value: T) : UserAnswers => UserAnswers = _.set(settable, value).success.value

    "generate Company Settlor Section" in {
      def businessSettlorBase(index: Int) = uaSet(living_settlor.SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Business) andThen
        uaSet(living_settlor.SettlorBusinessNamePage(index), "International Exports")

      def businessSettlorWithUTR(index: Int) = businessSettlorBase(index) andThen
        uaSet(living_settlor.SettlorUtrYesNoPage(index), true) andThen
        uaSet(living_settlor.SettlorUtrPage(index), "UTRUTRUTRUTR")

      def businessSettlorWithUKAddress(index: Int) = businessSettlorBase(index) andThen
        uaSet(living_settlor.SettlorUtrYesNoPage(index), false) andThen
        uaSet(living_settlor.SettlorAddressYesNoPage(index), true) andThen
        uaSet(living_settlor.SettlorAddressUKYesNoPage(index), true) andThen
        uaSet(living_settlor.SettlorAddressUKPage(index), UKAddress("Line1", "Line2", Some("Line3"), None, "POSTCODE"))

      def businessSettlorWithNonUKAddress(index: Int) = businessSettlorBase(index) andThen
        uaSet(living_settlor.SettlorUtrYesNoPage(index), false) andThen
        uaSet(living_settlor.SettlorAddressYesNoPage(index), true) andThen
        uaSet(living_settlor.SettlorAddressUKYesNoPage(index), false) andThen
        uaSet(living_settlor.SettlorAddressInternationalPage(index), InternationalAddress( "Line1", "Line2", Some("Line3"), "AN"))

      def businessSettlorWithNoIdentification(index: Int) = businessSettlorBase(index) andThen
        uaSet(living_settlor.SettlorUtrYesNoPage(index), false) andThen
        uaSet(living_settlor.SettlorAddressYesNoPage(index), false)

      def businessSettlorInEmployeeRelatedTrust(index: Int) = businessSettlorWithUKAddress(index) andThen
        uaSet(living_settlor.SettlorCompanyTypePage(index), Trading) andThen
        uaSet(living_settlor.SettlorCompanyTimePage(index), false)

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val result = helper.entities((
        businessSettlorWithUTR(0) andThen
        businessSettlorWithUKAddress(1) andThen
        businessSettlorWithNonUKAddress(2) andThen
        businessSettlorWithNoIdentification(3) andThen
        businessSettlorInEmployeeRelatedTrust(4)
        ).apply(emptyUserAnswers))


      result mustBe Seq(
        AnswerSection(None, Nil, Some("answerPage.section.settlors.heading")),
        AnswerSection(Some("Settlor 1"),Seq(
          AnswerRow("What is the business’s name?", Html("International Exports"), None, canEdit = false),
          AnswerRow("Do you know International Exports’s Unique Taxpayer Reference (UTR) number?", Html("Yes"), None, canEdit = false),
          AnswerRow("What is International Exports’s Unique Taxpayer Reference (UTR) number?", Html("UTRUTRUTRUTR"), None, canEdit = false)
          ), None),
        AnswerSection(Some("Settlor 2"), Seq(
          AnswerRow("What is the business’s name?", Html("International Exports"), None, canEdit = false),
          AnswerRow("Do you know International Exports’s Unique Taxpayer Reference (UTR) number?", Html("No"), None,canEdit = false),
          AnswerRow("Do you know International Exports’s address?", Html("Yes"), None,canEdit = false),
          AnswerRow("Is International Exports’s address in the UK?", Html("Yes"), None,canEdit = false),
          AnswerRow("What is International Exports’s address?", Html("Line1<br />Line2<br />Line3<br />POSTCODE"), None,canEdit = false)

        ), None),
        AnswerSection(Some("Settlor 3"), Seq(
          AnswerRow("What is the business’s name?", Html("International Exports"), None,canEdit = false),
          AnswerRow("Do you know International Exports’s Unique Taxpayer Reference (UTR) number?", Html("No"), None,canEdit = false),
          AnswerRow("Do you know International Exports’s address?", Html("Yes"), None,canEdit = false),
          AnswerRow("Is International Exports’s address in the UK?", Html("No"), None,canEdit = false),
          AnswerRow("What is International Exports’s address?", Html("Line1<br />Line2<br />Line3<br />Dutch Antilles"), None,canEdit = false)
        ), None),
        AnswerSection(Some("Settlor 4"), Seq(
          AnswerRow("What is the business’s name?", Html("International Exports"), None,canEdit = false),
          AnswerRow("Do you know International Exports’s Unique Taxpayer Reference (UTR) number?", Html("No"), None,canEdit = false),
          AnswerRow("Do you know International Exports’s address?", Html("No"), None,canEdit = false)
        ), None),
        AnswerSection(Some("Settlor 5"), Seq(
          AnswerRow("What is the business’s name?", Html("International Exports"), None,canEdit = false),
          AnswerRow("Do you know International Exports’s Unique Taxpayer Reference (UTR) number?", Html("No"), None,canEdit = false),
          AnswerRow("Do you know International Exports’s address?", Html("Yes"), None,canEdit = false),
          AnswerRow("Is International Exports’s address in the UK?", Html("Yes"), None,canEdit = false),
          AnswerRow("What is International Exports’s address?", Html("Line1<br />Line2<br />Line3<br />POSTCODE"), None,canEdit = false),
          AnswerRow("What kind of business is International Exports?", Html("Trading"), None,canEdit = false),
          AnswerRow("At the date of each contribution to the trust, had the business been in existence for at least 2 years?", Html("No"), None,canEdit = false)

        ), None)
      )
    }

    "generate Individual Settlor Section" in {
      //dob

      def baseIndividualSettlor(index: Int) =
        uaSet(living_settlor.SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual) andThen
        uaSet(living_settlor.SettlorIndividualNamePage(index), FullName("Joe", None,  "Bloggs")) andThen
        uaSet(living_settlor.SettlorIndividualDateOfBirthYesNoPage(index), true) andThen
        uaSet(living_settlor.SettlorIndividualDateOfBirthPage(index), LocalDate.parse("1934-12-12"))


      def individualSettlorWithNino(index: Int) = baseIndividualSettlor(index) andThen
        uaSet(living_settlor.SettlorIndividualNINOYesNoPage(index), true) andThen
        uaSet(living_settlor.SettlorIndividualNINOPage(index), "AA000000A")

      def individualSettlorWithUKAddressAndNoPassportOrIdCard(index: Int) = baseIndividualSettlor(index) andThen
        uaSet(living_settlor.SettlorIndividualNINOYesNoPage(index), false) andThen
        uaSet(living_settlor.SettlorAddressYesNoPage(index), true) andThen
        uaSet(living_settlor.SettlorAddressUKYesNoPage(index), true) andThen
        uaSet(living_settlor.SettlorAddressUKPage(index), UKAddress("Line1", "Line2", Some("Line3"), None, "POSTCODE")) andThen
        uaSet(living_settlor.SettlorIndividualPassportIDCardYesNoPage(index), false)

      def individualSettlorWithInternationalAddressAndIdCard(index: Int) = baseIndividualSettlor(index) andThen
        uaSet(living_settlor.SettlorIndividualNINOYesNoPage(index), false) andThen
        uaSet(living_settlor.SettlorAddressYesNoPage(index), true) andThen
        uaSet(living_settlor.SettlorAddressUKYesNoPage(index), false) andThen
        uaSet(living_settlor.SettlorAddressInternationalPage(index), InternationalAddress("Line1", "Line2", Some("Line3"), "DE")) andThen
        uaSet(living_settlor.SettlorIndividualPassportIDCardYesNoPage(index), true) andThen
        uaSet(living_settlor.SettlorIndividualPassportIDCardPage(index), PassportOrIdCardDetails("DE", "1234567890", LocalDate.of(2020, 1, 1)))

      def individualSettlorWithNoId(index: Int) = baseIndividualSettlor(index) andThen
        uaSet(living_settlor.SettlorIndividualNINOYesNoPage(index), false) andThen
        uaSet(living_settlor.SettlorAddressYesNoPage(index), false)

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val result = helper.entities((
        individualSettlorWithNino(0) andThen
          individualSettlorWithUKAddressAndNoPassportOrIdCard(1) andThen
          individualSettlorWithInternationalAddressAndIdCard(2) andThen
          individualSettlorWithNoId(3)
        ).apply(emptyUserAnswers))


      result mustBe Seq(
        AnswerSection(None, Nil, Some("answerPage.section.settlors.heading")),
        AnswerSection(Some("Settlor 1"),Seq(
          AnswerRow("What is the settlor’s name?", Html("Joe Bloggs"), None, canEdit = false),
          AnswerRow("Do you know Joe Bloggs’s date of birth?", Html("Yes"), None, canEdit = false),
          AnswerRow("What is Joe Bloggs’s date of birth?", Html("12 December 1934"), None, canEdit = false),
          AnswerRow("Do you know Joe Bloggs’s National Insurance number?", Html("Yes"), None, canEdit = false),
          AnswerRow("What is Joe Bloggs’s National Insurance number?", Html("AA 00 00 00 A"), None, canEdit = false)
        ), None),
        AnswerSection(Some("Settlor 2"),Seq(
          AnswerRow("What is the settlor’s name?", Html("Joe Bloggs"), None, canEdit = false),
          AnswerRow("Do you know Joe Bloggs’s date of birth?", Html("Yes"), None, canEdit = false),
          AnswerRow("What is Joe Bloggs’s date of birth?", Html("12 December 1934"), None, canEdit = false),
          AnswerRow("Do you know Joe Bloggs’s National Insurance number?", Html("No"), None, canEdit = false),
          AnswerRow("Do you know Joe Bloggs’s address?", Html("Yes"), None, canEdit = false),
          AnswerRow("Does Joe Bloggs live in the UK?", Html("Yes"), None, canEdit = false),
          AnswerRow("What is Joe Bloggs’s address?", Html("Line1<br />Line2<br />Line3<br />POSTCODE"), None, canEdit = false),
          AnswerRow("Do you know Joe Bloggs’s passport or ID card details?", Html("No"), None, canEdit = false)
        ), None),
        AnswerSection(Some("Settlor 3"),Seq(
          AnswerRow("What is the settlor’s name?", Html("Joe Bloggs"), None, canEdit = false),
          AnswerRow("Do you know Joe Bloggs’s date of birth?", Html("Yes"), None, canEdit = false),
          AnswerRow("What is Joe Bloggs’s date of birth?", Html("12 December 1934"), None, canEdit = false),
          AnswerRow("Do you know Joe Bloggs’s National Insurance number?", Html("No"), None, canEdit = false),
          AnswerRow("Do you know Joe Bloggs’s address?", Html("Yes"), None, canEdit = false),
          AnswerRow("Does Joe Bloggs live in the UK?", Html("No"), None, canEdit = false),
          AnswerRow("What is Joe Bloggs’s address?", Html("Line1<br />Line2<br />Line3<br />Germany"), None, canEdit = false),
          AnswerRow("Do you know Joe Bloggs’s passport or ID card details?", Html("Yes"), None, canEdit = false),
          AnswerRow("What are Joe Bloggs’s passport or ID card details?", Html("Germany<br />1234567890<br />1 January 2020"), None, canEdit = false)
        ), None),
        AnswerSection(Some("Settlor 4"),Seq(
          AnswerRow("What is the settlor’s name?", Html("Joe Bloggs"), None, canEdit = false),
          AnswerRow("Do you know Joe Bloggs’s date of birth?", Html("Yes"), None, canEdit = false),
          AnswerRow("What is Joe Bloggs’s date of birth?", Html("12 December 1934"), None, canEdit = false),
          AnswerRow("Do you know Joe Bloggs’s National Insurance number?", Html("No"), None, canEdit = false),
          AnswerRow("Do you know Joe Bloggs’s address?", Html("No"), None, canEdit = false)
        ), None)
      )
    }
  }

}
