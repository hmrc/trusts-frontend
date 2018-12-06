package pages

import pages.behaviours.PageBehaviours

class GovernedOutsideTheUKPageSpec extends PageBehaviours {

  "GovernedOutsideTheUKPage" must {

    beRetrievable[Boolean](GovernedOutsideTheUKPage)

    beSettable[Boolean](GovernedOutsideTheUKPage)

    beRemovable[Boolean](GovernedOutsideTheUKPage)
  }
}
