package pages

import models.FullName
import pages.behaviours.PageBehaviours

class SettlorIndividualNamePageSpec extends PageBehaviours {

  "SettlorIndividualNamePage" must {

    beRetrievable[FullName](SettlorIndividualNamePage)

    beSettable[FullName](SettlorIndividualNamePage)

    beRemovable[FullName](SettlorIndividualNamePage)
  }
}
