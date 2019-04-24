package pages

import pages.behaviours.PageBehaviours


class IndividualBeneficiaryNationalInsuranceNumberPageSpec extends PageBehaviours {

  "IndividualBeneficiaryNationalInsuranceNumberPage" must {

    beRetrievable[String](IndividualBeneficiaryNationalInsuranceNumberPage)

    beSettable[String](IndividualBeneficiaryNationalInsuranceNumberPage)

    beRemovable[String](IndividualBeneficiaryNationalInsuranceNumberPage)
  }
}
