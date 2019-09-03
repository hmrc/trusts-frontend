package pages

import pages.behaviours.PageBehaviours

class SettlorHandoverReliefYesNoPageSpec extends PageBehaviours {

  "SettlorHandoverReliefYesNoPage" must {

    beRetrievable[Boolean](SettlorHandoverReliefYesNoPage)

    beSettable[Boolean](SettlorHandoverReliefYesNoPage)

    beRemovable[Boolean](SettlorHandoverReliefYesNoPage)
  }
}
