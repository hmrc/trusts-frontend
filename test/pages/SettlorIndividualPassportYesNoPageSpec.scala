package pages

import pages.behaviours.PageBehaviours

class SettlorIndividualPassportYesNoPageSpec extends PageBehaviours {

  "SettlorIndividualPassportYesNoPage" must {

    beRetrievable[Boolean](SettlorIndividualPassportYesNoPage)

    beSettable[Boolean](SettlorIndividualPassportYesNoPage)

    beRemovable[Boolean](SettlorIndividualPassportYesNoPage)
  }
}
