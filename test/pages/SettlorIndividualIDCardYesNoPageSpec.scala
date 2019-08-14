package pages

import pages.behaviours.PageBehaviours

class SettlorIndividualIDCardYesNoPageSpec extends PageBehaviours {

  "SettlorIndividualIDCardYesNoPage" must {

    beRetrievable[Boolean](SettlorIndividualIDCardYesNoPage)

    beSettable[Boolean](SettlorIndividualIDCardYesNoPage)

    beRemovable[Boolean](SettlorIndividualIDCardYesNoPage)
  }
}
