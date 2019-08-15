package pages

import pages.behaviours.PageBehaviours

class SettlorIndividualNINOYesNoPageSpec extends PageBehaviours {

  "SettlorIndividualNINOYesNoPage" must {

    beRetrievable[Boolean](SettlorIndividualNINOYesNoPage)

    beSettable[Boolean](SettlorIndividualNINOYesNoPage)

    beRemovable[Boolean](SettlorIndividualNINOYesNoPage)
  }
}
