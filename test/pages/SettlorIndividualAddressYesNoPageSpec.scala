package pages

import pages.behaviours.PageBehaviours

class SettlorIndividualAddressYesNoPageSpec extends PageBehaviours {

  "SettlorIndividualAddressYesNoPage" must {

    beRetrievable[Boolean](SettlorIndividualAddressYesNoPage)

    beSettable[Boolean](SettlorIndividualAddressYesNoPage)

    beRemovable[Boolean](SettlorIndividualAddressYesNoPage)
  }
}
