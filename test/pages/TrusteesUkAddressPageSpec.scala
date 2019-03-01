package pages

import models.TrusteesUkAddress
import pages.behaviours.PageBehaviours

class TrusteesUkAddressPageSpec extends PageBehaviours {

  "TrusteesUkAddressPage" must {

    beRetrievable[TrusteesUkAddress](TrusteesUkAddressPage)

    beSettable[TrusteesUkAddress](TrusteesUkAddressPage)

    beRemovable[TrusteesUkAddress](TrusteesUkAddressPage)
  }
}
