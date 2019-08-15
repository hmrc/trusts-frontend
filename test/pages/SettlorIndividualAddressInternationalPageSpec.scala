package pages

import models.SettlorIndividualAddressInternational
import pages.behaviours.PageBehaviours

class SettlorIndividualAddressInternationalPageSpec extends PageBehaviours {

  "SettlorIndividualAddressInternationalPage" must {

    beRetrievable[SettlorIndividualAddressInternational](SettlorIndividualAddressInternationalPage)

    beSettable[SettlorIndividualAddressInternational](SettlorIndividualAddressInternationalPage)

    beRemovable[SettlorIndividualAddressInternational](SettlorIndividualAddressInternationalPage)
  }
}
