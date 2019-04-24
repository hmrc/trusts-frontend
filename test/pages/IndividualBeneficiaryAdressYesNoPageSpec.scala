package pages

import pages.behaviours.PageBehaviours

class IndividualBeneficiaryAdressYesNoPageSpec extends PageBehaviours {

  "IndividualBeneficiaryAdressYesNoPage" must {

    beRetrievable[Boolean](IndividualBeneficiaryAdressYesNoPage)

    beSettable[Boolean](IndividualBeneficiaryAdressYesNoPage)

    beRemovable[Boolean](IndividualBeneficiaryAdressYesNoPage)
  }
}
