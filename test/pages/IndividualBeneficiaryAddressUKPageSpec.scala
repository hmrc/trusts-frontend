package pages

import models.IndividualBeneficiaryAddressUK
import pages.behaviours.PageBehaviours

class IndividualBeneficiaryAddressUKPageSpec extends PageBehaviours {

  "IndividualBeneficiaryAddressUKPage" must {

    beRetrievable[IndividualBeneficiaryAddressUK](IndividualBeneficiaryAddressUKPage)

    beSettable[IndividualBeneficiaryAddressUK](IndividualBeneficiaryAddressUKPage)

    beRemovable[IndividualBeneficiaryAddressUK](IndividualBeneficiaryAddressUKPage)
  }
}
