package pages

import pages.behaviours.PageBehaviours

class IndividualBeneficiaryVulnerableYesNoPageSpec extends PageBehaviours {

  "IndividualBeneficiaryVulnerableYesNoPage" must {

    beRetrievable[Boolean](IndividualBeneficiaryVulnerableYesNoPage)

    beSettable[Boolean](IndividualBeneficiaryVulnerableYesNoPage)

    beRemovable[Boolean](IndividualBeneficiaryVulnerableYesNoPage)
  }
}
