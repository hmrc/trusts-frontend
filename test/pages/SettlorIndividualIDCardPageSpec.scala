package pages

import models.SettlorIndividualIDCard
import pages.behaviours.PageBehaviours

class SettlorIndividualIDCardPageSpec extends PageBehaviours {

  "SettlorIndividualIDCardPage" must {

    beRetrievable[SettlorIndividualIDCard](SettlorIndividualIDCardPage)

    beSettable[SettlorIndividualIDCard](SettlorIndividualIDCardPage)

    beRemovable[SettlorIndividualIDCard](SettlorIndividualIDCardPage)
  }
}
