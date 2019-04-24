package pages

import pages.behaviours.PageBehaviours


class IndividualBeneficiaryIncomePageSpec extends PageBehaviours {

  "IndividualBeneficiaryIncomePage" must {

    beRetrievable[String](IndividualBeneficiaryIncomePage)

    beSettable[String](IndividualBeneficiaryIncomePage)

    beRemovable[String](IndividualBeneficiaryIncomePage)
  }
}
