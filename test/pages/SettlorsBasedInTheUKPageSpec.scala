package pages

import pages.behaviours.PageBehaviours

class SettlorsBasedInTheUKPageSpec extends PageBehaviours {

  "SettlorsBasedInTheUKPage" must {

    beRetrievable[Boolean](SettlorsBasedInTheUKPage)

    beSettable[Boolean](SettlorsBasedInTheUKPage)

    beRemovable[Boolean](SettlorsBasedInTheUKPage)
  }
}
