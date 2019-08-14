package pages

import models.SettlorIndividualAddress
import pages.behaviours.PageBehaviours

class SettlorIndividualAddressPageSpec extends PageBehaviours {

  "SettlorIndividualAddressPage" must {

    beRetrievable[SettlorIndividualAddress](SettlorIndividualAddressPage)

    beSettable[SettlorIndividualAddress](SettlorIndividualAddressPage)

    beRemovable[SettlorIndividualAddress](SettlorIndividualAddressPage)
  }
}
