package pages

import models.SettlorIndividualAddressUK
import pages.behaviours.PageBehaviours

class SettlorIndividualAddressUKPageSpec extends PageBehaviours {

  "SettlorIndividualAddressUKPage" must {

    beRetrievable[SettlorIndividualAddressUK](SettlorIndividualAddressUKPage)

    beSettable[SettlorIndividualAddressUK](SettlorIndividualAddressUKPage)

    beRemovable[SettlorIndividualAddressUK](SettlorIndividualAddressUKPage)
  }
}
