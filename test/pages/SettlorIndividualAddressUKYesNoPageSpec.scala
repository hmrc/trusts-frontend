package pages

import pages.behaviours.PageBehaviours

class SettlorIndividualAddressUKYesNoPageSpec extends PageBehaviours {

  "SettlorIndividualAddressUKYesNoPage" must {

    beRetrievable[Boolean](SettlorIndividualAddressUKYesNoPage)

    beSettable[Boolean](SettlorIndividualAddressUKYesNoPage)

    beRemovable[Boolean](SettlorIndividualAddressUKYesNoPage)
  }
}
