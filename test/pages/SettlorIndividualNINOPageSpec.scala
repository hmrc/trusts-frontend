package pages

import pages.behaviours.PageBehaviours


class SettlorIndividualNINOPageSpec extends PageBehaviours {

  "SettlorIndividualNINOPage" must {

    beRetrievable[String](SettlorIndividualNINOPage)

    beSettable[String](SettlorIndividualNINOPage)

    beRemovable[String](SettlorIndividualNINOPage)
  }
}
