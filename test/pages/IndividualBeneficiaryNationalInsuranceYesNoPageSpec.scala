package pages

import pages.behaviours.PageBehaviours

class IndividualBeneficiaryNationalInsuranceYesNoPageSpec extends PageBehaviours {

  "IndividualBeneficiaryNationalInsuranceYesNoPage" must {

    beRetrievable[Boolean](IndividualBeneficiaryNationalInsuranceYesNoPage)

    beSettable[Boolean](IndividualBeneficiaryNationalInsuranceYesNoPage)

    beRemovable[Boolean](IndividualBeneficiaryNationalInsuranceYesNoPage)
  }
}
