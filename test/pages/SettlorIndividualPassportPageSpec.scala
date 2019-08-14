package pages

import models.SettlorIndividualPassport
import pages.behaviours.PageBehaviours

class SettlorIndividualPassportPageSpec extends PageBehaviours {

  "SettlorIndividualPassportPage" must {

    beRetrievable[SettlorIndividualPassport](SettlorIndividualPassportPage)

    beSettable[SettlorIndividualPassport](SettlorIndividualPassportPage)

    beRemovable[SettlorIndividualPassport](SettlorIndividualPassportPage)
  }
}
