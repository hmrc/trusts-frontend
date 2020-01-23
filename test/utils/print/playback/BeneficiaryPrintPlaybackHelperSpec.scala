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
import models.core.pages.{Description, FullName, UKAddress}
import models.playback.UserAnswers
import models.registration.pages.{PassportOrIdCardDetails, RoleInCompany}
import pages.register.TrustNamePage
import pages.register.beneficiaries.charity._
import pages.register.beneficiaries.company._
import pages.register.beneficiaries.trust._
import pages.register.beneficiaries.individual._
import pages.register.beneficiaries.large._
import pages.register.beneficiaries.other._
import pages.register.beneficiaries.classOfBeneficiary._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class BeneficiaryPrintPlaybackHelperSpec extends PlaybackSpecBase {

  "Beneficiary print playback helper" must {

    "generate charity beneficiaries sections" in {

      val charityBen1Name = "Red Cross Ltd."
      val charityBen2Name = "Bernardos"

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = emptyUserAnswers
        .set(CharityBeneficiaryNamePage(0), charityBen1Name).success.value
        .set(CharityBeneficiaryDiscretionYesNoPage(0), false).success.value
        .set(CharityBeneficiaryShareOfIncomePage(0), "98").success.value
        .set(CharityBeneficiaryAddressYesNoPage(0), true).success.value
        .set(CharityBeneficiaryAddressUKYesNoPage(0), true).success.value
        .set(CharityBeneficiaryAddressPage(0),
          UKAddress(
            line1 = "line1",
            line2 = "line2",
            line3 = Some("line3"),
            line4 = Some("line4"),
            postcode = "NE981ZZ"
          )
        ).success.value

        .set(CharityBeneficiaryNamePage(1), charityBen2Name).success.value
        .set(CharityBeneficiaryDiscretionYesNoPage(1), true).success.value
        .set(CharityBeneficiaryAddressYesNoPage(1), false).success.value
        .set(CharityBeneficiaryUtrPage(1), "1234567890").success.value

      val result = helper.entities(answers)

      result mustBe Seq(
        AnswerSection(None, Nil, Some("answerPage.section.beneficiaries.heading")),
        AnswerSection(
          headingKey = Some("Charity beneficiary 1"),
          rows = Seq(
            AnswerRow(label = "charityBeneficiaryName.checkYourAnswersLabel", answer = Html("Red Cross Ltd."), changeUrl = None),
            AnswerRow(label = "charityBeneficiaryShareOfIncomeYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = None, labelArg = charityBen1Name),
            AnswerRow(label = "charityBeneficiaryShareOfIncome.checkYourAnswersLabel", answer = Html("98%"), changeUrl = None, labelArg = charityBen1Name),
            AnswerRow(label = "charityBeneficiaryAddressYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = None, labelArg = charityBen1Name),
            AnswerRow(label = "charityBeneficiaryAddressUKYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = None, labelArg = charityBen1Name),
            AnswerRow(label = "charityBeneficiaryAddress.checkYourAnswersLabel", answer = Html("line1<br />line2<br />line3<br />line4<br />NE981ZZ"), changeUrl = None, labelArg = charityBen1Name)
          ),
          sectionKey = None
        ),
        AnswerSection(
          headingKey = Some("Charity beneficiary 2"),
          rows = Seq(
            AnswerRow(label = "charityBeneficiaryName.checkYourAnswersLabel", answer = Html("Bernardos"), changeUrl = None),
            AnswerRow(label = "charityBeneficiaryShareOfIncomeYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = None, labelArg = charityBen2Name),
            AnswerRow(label = "charityBeneficiaryAddressYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = None, labelArg = charityBen2Name),
            AnswerRow(label = "charityBeneficiaryUtr.checkYourAnswersLabel", answer = Html("1234567890"), changeUrl = None, labelArg = charityBen2Name)
          ),
          sectionKey = None
        )
      )

    }

    "generate individual beneficiaries sections" in {

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = emptyUserAnswers
        .set(IndividualBeneficiaryRoleInCompanyPage(0), RoleInCompany.Director).success.value
        .set(IndividualBeneficiaryNamePage(0), FullName("Michael", None, "Finnegan")).success.value
        .set(IndividualBeneficiaryDateOfBirthYesNoPage(0), true).success.value
        .set(IndividualBeneficiaryDateOfBirthPage(0), LocalDate.of(1996, 2, 3)).success.value
        .set(IndividualBeneficiaryIncomeYesNoPage(0), true).success.value
        .set(IndividualBeneficiaryIncomePage(0), "98").success.value
        .set(IndividualBeneficiaryNationalInsuranceYesNoPage(0), true).success.value
        .set(IndividualBeneficiaryNationalInsuranceNumberPage(0), "JB 12 34 56 C").success.value
        .set(IndividualBeneficiaryVulnerableYesNoPage(0), true).success.value

        .set(IndividualBeneficiaryRoleInCompanyPage(1), RoleInCompany.Employee).success.value
        .set(IndividualBeneficiaryNamePage(1), FullName("Joe", None, "Bloggs")).success.value
        .set(IndividualBeneficiaryDateOfBirthYesNoPage(1), false).success.value
        .set(IndividualBeneficiaryIncomeYesNoPage(1), false).success.value
        .set(IndividualBeneficiaryNationalInsuranceYesNoPage(1), false).success.value
        .set(IndividualBeneficiaryAddressYesNoPage(1), false).success.value
        .set(IndividualBeneficiaryVulnerableYesNoPage(1), true).success.value

        .set(IndividualBeneficiaryRoleInCompanyPage(2), RoleInCompany.NA).success.value
        .set(IndividualBeneficiaryNamePage(2), FullName("Paul", None, "Chuckle")).success.value
        .set(IndividualBeneficiaryDateOfBirthYesNoPage(2), false).success.value
        .set(IndividualBeneficiaryIncomeYesNoPage(2), false).success.value
        .set(IndividualBeneficiaryNationalInsuranceYesNoPage(2), false).success.value
        .set(IndividualBeneficiaryAddressYesNoPage(2), true).success.value
        .set(IndividualBeneficiaryAddressUKYesNoPage(2), true).success.value
        .set(IndividualBeneficiaryAddressPage(2), UKAddress("line 1", "line 2", None, None, "NE11NE")).success.value
        .set(IndividualBeneficiaryPassportIDCardYesNoPage(2), true).success.value
        .set(IndividualBeneficiaryPassportIDCardPage(2), PassportOrIdCardDetails("DE", "KSJDFKSDHF6456545147852369QWER", LocalDate.of(2020,2,2))).success.value
        .set(IndividualBeneficiaryVulnerableYesNoPage(2), false).success.value

      val result = helper.entities(answers)

      val name1 = "Michael Finnegan"
      val name2 = "Joe Bloggs"
      val name3 = "Paul Chuckle"

      result mustBe Seq(
        AnswerSection(None, Nil, Some("answerPage.section.beneficiaries.heading")),
        AnswerSection(
          headingKey = Some("Individual beneficiary 1"),
          rows = Seq(
            AnswerRow(label = messages("individualBeneficiaryName.checkYourAnswersLabel"), answer = Html(name1), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryRoleInCompany.checkYourAnswersLabel", name1), answer = Html("Director"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryDateOfBirthYesNo.checkYourAnswersLabel", name1), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryDateOfBirth.checkYourAnswersLabel", name1), answer = Html("3 February 1996"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryIncomeYesNo.checkYourAnswersLabel", name1), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryIncome.checkYourAnswersLabel", name1), answer = Html("98%"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryNationalInsuranceYesNo.checkYourAnswersLabel", name1), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryNationalInsuranceNumber.checkYourAnswersLabel", name1), answer = Html("JB  1 2  34  5 6  C"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryVulnerableYesNo.checkYourAnswersLabel", name1), answer = Html("Yes"), changeUrl = None)
          ),
          sectionKey = None
        ),
        AnswerSection(
          headingKey = Some("Individual beneficiary 2"),
          rows = Seq(
            AnswerRow(label = messages("individualBeneficiaryName.checkYourAnswersLabel"), answer = Html(name2), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryRoleInCompany.checkYourAnswersLabel", name2), answer = Html("Employee"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryDateOfBirthYesNo.checkYourAnswersLabel", name2), answer = Html("No"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryIncomeYesNo.checkYourAnswersLabel", name2), answer = Html("No"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryNationalInsuranceYesNo.checkYourAnswersLabel", name2), answer = Html("No"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryAddressYesNo.checkYourAnswersLabel", name2), answer = Html("No"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryVulnerableYesNo.checkYourAnswersLabel", name2), answer = Html("Yes"), changeUrl = None)
          ),
          sectionKey = None
        ),
        AnswerSection(
          headingKey = Some("Individual beneficiary 3"),
          rows = Seq(
            AnswerRow(label = messages("individualBeneficiaryName.checkYourAnswersLabel"), answer = Html(name3), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryRoleInCompany.checkYourAnswersLabel", name3), answer = Html("NA"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryDateOfBirthYesNo.checkYourAnswersLabel", name3), answer = Html("No"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryIncomeYesNo.checkYourAnswersLabel", name3), answer = Html("No"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryNationalInsuranceYesNo.checkYourAnswersLabel", name3), answer = Html("No"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryAddressYesNo.checkYourAnswersLabel", name3), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryAddressUKYesNo.checkYourAnswersLabel", name3), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryAddressUK.checkYourAnswersLabel", name3), answer = Html("line 1<br />line 2<br />NE11NE"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryPassportIDCardYesNo.checkYourAnswersLabel", name3), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryPassportIDCard.checkYourAnswersLabel", name3), answer = Html("Germany<br />KSJDFKSDHF6456545147852369QWER<br />2 February 2020"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiaryVulnerableYesNo.checkYourAnswersLabel", name3), answer = Html("No"), changeUrl = None)
          ),
          sectionKey = None
        )
      )

    }

    "generate company beneficiaries sections" in {

      val companyBen1Name = "Amazon"
      val companyBen2Name = "Apple"

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = emptyUserAnswers
        .set(CompanyBeneficiaryNamePage(0), companyBen1Name).success.value
        .set(CompanyBeneficiaryDiscretionYesNoPage(0), false).success.value
        .set(CompanyBeneficiaryShareOfIncomePage(0), "98").success.value
        .set(CompanyBeneficiaryAddressYesNoPage(0), true).success.value
        .set(CompanyBeneficiaryAddressUKYesNoPage(0), true).success.value
        .set(CompanyBeneficiaryAddressPage(0),
          UKAddress(
            line1 = "line1",
            line2 = "line2",
            line3 = Some("line3"),
            line4 = Some("line4"),
            postcode = "NE981ZZ"
          )
        ).success.value

        .set(CompanyBeneficiaryNamePage(1), companyBen2Name).success.value
        .set(CompanyBeneficiaryDiscretionYesNoPage(1), true).success.value
        .set(CompanyBeneficiaryAddressYesNoPage(1), false).success.value
        .set(CompanyBeneficiaryUtrPage(1), "1234567890").success.value

      val result = helper.entities(answers)

      result mustBe Seq(
        AnswerSection(None, Nil, Some("answerPage.section.beneficiaries.heading")),
        AnswerSection(
          headingKey = Some("Company beneficiary 1"),
          rows = Seq(
            AnswerRow(label = messages("companyBeneficiaryName.checkYourAnswersLabel"), answer = Html("Amazon"), changeUrl = None),
            AnswerRow(label = messages("companyBeneficiaryShareOfIncomeYesNo.checkYourAnswersLabel", companyBen1Name), answer = Html("No"), changeUrl = None),
            AnswerRow(label = messages("companyBeneficiaryShareOfIncome.checkYourAnswersLabel", companyBen1Name), answer = Html("98%"), changeUrl = None),
            AnswerRow(label = messages("companyBeneficiaryAddressYesNo.checkYourAnswersLabel", companyBen1Name), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("companyBeneficiaryAddressUKYesNo.checkYourAnswersLabel", companyBen1Name), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("companyBeneficiaryAddress.checkYourAnswersLabel", companyBen1Name), answer = Html("line1<br />line2<br />line3<br />line4<br />NE981ZZ"), changeUrl = None)
          ),
          sectionKey = None
        ),
        AnswerSection(
          headingKey = Some("Company beneficiary 2"),
          rows = Seq(
            AnswerRow(label = messages("companyBeneficiaryName.checkYourAnswersLabel", companyBen2Name), answer = Html("Apple"), changeUrl = None),
            AnswerRow(label = messages("companyBeneficiaryShareOfIncomeYesNo.checkYourAnswersLabel", companyBen2Name), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("companyBeneficiaryAddressYesNo.checkYourAnswersLabel", companyBen2Name), answer = Html("No"), changeUrl = None),
            AnswerRow(label = messages("companyBeneficiaryUtr.checkYourAnswersLabel", companyBen2Name), answer = Html("1234567890"), changeUrl = None)
          ),
          sectionKey = None
        )
      )

    }

    "generate trust beneficiaries sections" in {

      val trustBen1Name = "Trust of Adam"
      val trustBen2Name = "Grandchildren of Adam"

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = emptyUserAnswers
        .set(TrustBeneficiaryNamePage(0), trustBen1Name).success.value
        .set(TrustBeneficiaryDiscretionYesNoPage(0), false).success.value
        .set(TrustBeneficiaryShareOfIncomePage(0), "98").success.value
        .set(TrustBeneficiaryAddressYesNoPage(0), true).success.value
        .set(TrustBeneficiaryAddressUKYesNoPage(0), true).success.value
        .set(TrustBeneficiaryAddressPage(0),
          UKAddress(
            line1 = "line1",
            line2 = "line2",
            line3 = Some("line3"),
            line4 = Some("line4"),
            postcode = "NE981ZZ"
          )
        ).success.value

        .set(TrustBeneficiaryNamePage(1), trustBen2Name).success.value
        .set(TrustBeneficiaryDiscretionYesNoPage(1), true).success.value
        .set(TrustBeneficiaryAddressYesNoPage(1), false).success.value
        .set(TrustBeneficiaryUtrPage(1), "1234567890").success.value

      val result = helper.entities(answers)

      result mustBe Seq(
        AnswerSection(None, Nil, Some("answerPage.section.beneficiaries.heading")),
        AnswerSection(
          headingKey = Some("Trust beneficiary 1"),
          rows = Seq(
            AnswerRow(label = messages("trustBeneficiaryName.checkYourAnswersLabel"), answer = Html("Trust of Adam"), changeUrl = None),
            AnswerRow(label = messages("trustBeneficiaryShareOfIncomeYesNo.checkYourAnswersLabel", trustBen1Name), answer = Html("No"), changeUrl = None),
            AnswerRow(label = messages("trustBeneficiaryShareOfIncome.checkYourAnswersLabel",trustBen1Name), answer = Html("98%"), changeUrl = None),
            AnswerRow(label = messages("trustBeneficiaryAddressYesNo.checkYourAnswersLabel",trustBen1Name), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("trustBeneficiaryAddressUKYesNo.checkYourAnswersLabel",trustBen1Name), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("trustBeneficiaryAddress.checkYourAnswersLabel",trustBen1Name), answer = Html("line1<br />line2<br />line3<br />line4<br />NE981ZZ"), changeUrl = None)
          ),
          sectionKey = None
        ),
        AnswerSection(
          headingKey = Some("Trust beneficiary 2"),
          rows = Seq(
            AnswerRow(label = messages("trustBeneficiaryName.checkYourAnswersLabel"), answer = Html("Grandchildren of Adam"), changeUrl = None),
            AnswerRow(label = messages("trustBeneficiaryShareOfIncomeYesNo.checkYourAnswersLabel",trustBen2Name), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("trustBeneficiaryAddressYesNo.checkYourAnswersLabel",trustBen2Name), answer = Html("No"), changeUrl = None),
            AnswerRow(label = messages("trustBeneficiaryUtr.checkYourAnswersLabel",trustBen2Name), answer = Html("1234567890"), changeUrl = None)
          ),
          sectionKey = None
        )
      )

    }

    "generate large beneficiaries sections" in {

      val largeBen1Name = "Amazon"
      val largeBen2Name = "Apple"

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = emptyUserAnswers
        .set(LargeBeneficiaryNamePage(0), largeBen1Name).success.value
        .set(LargeBeneficiaryAddressYesNoPage(0), true).success.value
        .set(LargeBeneficiaryAddressUKYesNoPage(0), true).success.value
        .set(LargeBeneficiaryAddressPage(0),
          UKAddress(
            line1 = "line1",
            line2 = "line2",
            line3 = Some("line3"),
            line4 = Some("line4"),
            postcode = "NE981ZZ"
          )
        ).success.value
        .set(LargeBeneficiaryDescriptionPage(0), Description("Description", None, None, None, None)).success.value
        .set(LargeBeneficiaryNumberOfBeneficiariesPage(0), "1").success.value

        .set(LargeBeneficiaryNamePage(1), largeBen2Name).success.value
        .set(LargeBeneficiaryDiscretionYesNoPage(1), true).success.value
        .set(LargeBeneficiaryAddressYesNoPage(1), false).success.value
        .set(LargeBeneficiaryUtrPage(1), "1234567890").success.value
        .set(LargeBeneficiaryDescriptionPage(1), Description("Description", None, None, None, None)).success.value
        .set(LargeBeneficiaryNumberOfBeneficiariesPage(1), "1").success.value

      val result = helper.entities(answers)

      result mustBe Seq(
        AnswerSection(None, Nil, Some("answerPage.section.beneficiaries.heading")),
        AnswerSection(
          headingKey = Some("Employment related beneficiary 1"),
          rows = Seq(
            AnswerRow(label = messages("largeBeneficiaryName.checkYourAnswersLabel"), answer = Html("Amazon"), changeUrl = None),
            AnswerRow(label = messages("largeBeneficiaryAddressYesNo.checkYourAnswersLabel", largeBen1Name), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("largeBeneficiaryAddressUKYesNo.checkYourAnswersLabel", largeBen1Name), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("largeBeneficiaryAddress.checkYourAnswersLabel", largeBen1Name), answer = Html("line1<br />line2<br />line3<br />line4<br />NE981ZZ"), changeUrl = None),
            AnswerRow(label = messages("largeBeneficiaryDescription.checkYourAnswersLabel", largeBen1Name), answer = Html("Description"), changeUrl = None),
            AnswerRow(label = messages("largeBeneficiaryNumberOfBeneficiaries.checkYourAnswersLabel", largeBen1Name), answer = Html("1"), changeUrl = None)
          ),
          sectionKey = None
        ),
        AnswerSection(
          headingKey = Some("Employment related beneficiary 2"),
          rows = Seq(
            AnswerRow(label = messages("largeBeneficiaryName.checkYourAnswersLabel", largeBen2Name), answer = Html("Apple"), changeUrl = None),
            AnswerRow(label = messages("largeBeneficiaryAddressYesNo.checkYourAnswersLabel", largeBen2Name), answer = Html("No"), changeUrl = None),
            AnswerRow(label = messages("largeBeneficiaryUtr.checkYourAnswersLabel", largeBen2Name), answer = Html("1234567890"), changeUrl = None),
            AnswerRow(label = messages("largeBeneficiaryDescription.checkYourAnswersLabel", largeBen2Name), answer = Html("Description"), changeUrl = None),
            AnswerRow(label = messages("largeBeneficiaryNumberOfBeneficiaries.checkYourAnswersLabel", largeBen2Name), answer = Html("1"), changeUrl = None)
          ),
          sectionKey = None
        )
      )

    }

    "generate other beneficiaries sections" in {

      val otherBen1Name = "Dog"
      val otherBen2Name = "Cat"

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = emptyUserAnswers
        .set(OtherBeneficiaryDescriptionPage(0), otherBen1Name).success.value
        .set(OtherBeneficiaryDiscretionYesNoPage(0), false).success.value
        .set(OtherBeneficiaryShareOfIncomePage(0), "98").success.value
        .set(OtherBeneficiaryAddressYesNoPage(0), true).success.value
        .set(OtherBeneficiaryAddressUKYesNoPage(0), true).success.value
        .set(OtherBeneficiaryAddressPage(0),
          UKAddress(
            line1 = "line1",
            line2 = "line2",
            line3 = Some("line3"),
            line4 = Some("line4"),
            postcode = "NE981ZZ"
          )
        ).success.value

        .set(OtherBeneficiaryDescriptionPage(1), otherBen2Name).success.value
        .set(OtherBeneficiaryDiscretionYesNoPage(1), true).success.value
        .set(OtherBeneficiaryAddressYesNoPage(1), false).success.value

      val result = helper.entities(answers)

      result mustBe Seq(
        AnswerSection(None, Nil, Some("answerPage.section.beneficiaries.heading")),
        AnswerSection(
          headingKey = Some("Other beneficiary 1"),
          rows = Seq(
            AnswerRow(label = messages("otherBeneficiaryDescription.checkYourAnswersLabel"), answer = Html("Dog"), changeUrl = None),
            AnswerRow(label = messages("otherBeneficiaryShareOfIncomeYesNo.checkYourAnswersLabel", otherBen1Name), answer = Html("No"), changeUrl = None),
            AnswerRow(label = messages("otherBeneficiaryShareOfIncome.checkYourAnswersLabel",otherBen1Name), answer = Html("98%"), changeUrl = None),
            AnswerRow(label = messages("otherBeneficiaryAddressYesNo.checkYourAnswersLabel",otherBen1Name), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("otherBeneficiaryAddressUKYesNo.checkYourAnswersLabel",otherBen1Name), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("otherBeneficiaryAddress.checkYourAnswersLabel",otherBen1Name), answer = Html("line1<br />line2<br />line3<br />line4<br />NE981ZZ"), changeUrl = None)
          ),
          sectionKey = None
        ),
        AnswerSection(
          headingKey = Some("Other beneficiary 2"),
          rows = Seq(
            AnswerRow(label = messages("otherBeneficiaryDescription.checkYourAnswersLabel"), answer = Html("Cat"), changeUrl = None),
            AnswerRow(label = messages("otherBeneficiaryShareOfIncomeYesNo.checkYourAnswersLabel",otherBen2Name), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("otherBeneficiaryAddressYesNo.checkYourAnswersLabel",otherBen2Name), answer = Html("No"), changeUrl = None)
          ),
          sectionKey = None
        )
      )

    }

    "generate class of beneficiaries sections" in {

      val classBenDescription1 = "Grandchildren"
      val classBenDescription2 = "Spouses"

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = emptyUserAnswers
        .set(ClassOfBeneficiaryDescriptionPage(0), classBenDescription1).success.value
        .set(ClassOfBeneficiaryDescriptionPage(1), classBenDescription2).success.value

      val result = helper.entities(answers)

      result mustBe Seq(
        AnswerSection(None, Nil, Some("answerPage.section.beneficiaries.heading")),
        AnswerSection(
          headingKey = Some("Class of beneficiary 1"),
          rows = Seq(
            AnswerRow(label = messages("classBeneficiaryDescription.checkYourAnswersLabel"), answer = Html("Grandchildren"), changeUrl = None)
          ),
          sectionKey = None
        ),
        AnswerSection(
          headingKey = Some("Class of beneficiary 2"),
          rows = Seq(
            AnswerRow(label = messages("classBeneficiaryDescription.checkYourAnswersLabel"), answer = Html("Spouses"), changeUrl = None)
          ),
          sectionKey = None
        )
      )

    }

  }

}