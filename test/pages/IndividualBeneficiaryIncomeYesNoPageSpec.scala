package pages

import pages.behaviours.PageBehaviours

class IndividualBeneficiaryIncomeYesNoPageSpec extends PageBehaviours {

  "IndividualBeneficiaryIncomeYesNoPage" must {

    beRetrievable[Boolean](IndividualBeneficiaryIncomeYesNoPage)

    beSettable[Boolean](IndividualBeneficiaryIncomeYesNoPage)

    beRemovable[Boolean](IndividualBeneficiaryIncomeYesNoPage)
  }
}
