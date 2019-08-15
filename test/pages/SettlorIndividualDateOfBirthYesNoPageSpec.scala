package pages

import pages.behaviours.PageBehaviours

class SettlorIndividualDateOfBirthYesNoPageSpec extends PageBehaviours {

  "SettlorIndividualDateOfBirthYesNoPage" must {

    beRetrievable[Boolean](SettlorIndividualDateOfBirthYesNoPage)

    beSettable[Boolean](SettlorIndividualDateOfBirthYesNoPage)

    beRemovable[Boolean](SettlorIndividualDateOfBirthYesNoPage)
  }
}
