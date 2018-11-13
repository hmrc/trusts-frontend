package pages

import pages.behaviours.PageBehaviours

class TrustAddressUKYesNoPageSpec extends PageBehaviours {

  "TrustAddressUKYesNoPage" must {

    beRetrievable[Boolean](TrustAddressUKYesNoPage)

    beSettable[Boolean](TrustAddressUKYesNoPage)

    beRemovable[Boolean](TrustAddressUKYesNoPage)
  }
}
