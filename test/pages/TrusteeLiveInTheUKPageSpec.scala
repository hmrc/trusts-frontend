package pages

import pages.behaviours.PageBehaviours

class TrusteeLiveInTheUKPageSpec extends PageBehaviours {

  "TrusteeLiveInTheUKPage" must {

    beRetrievable[Boolean](TrusteeLiveInTheUKPage)

    beSettable[Boolean](TrusteeLiveInTheUKPage)

    beRemovable[Boolean](TrusteeLiveInTheUKPage)
  }
}
