package pages

import pages.behaviours.PageBehaviours

class IndividualBeneficiaryIsAddressUKPageSpec extends PageBehaviours {

  "IndividualBeneficiaryIsAddressUKPage" must {

    beRetrievable[Boolean](IndividualBeneficiaryIsAddressUKPage)

    beSettable[Boolean](IndividualBeneficiaryIsAddressUKPage)

    beRemovable[Boolean](IndividualBeneficiaryIsAddressUKPage)
  }
}
