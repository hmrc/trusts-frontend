package pages

import pages.behaviours.PageBehaviours

class TrustResidentInUKPageSpec extends PageBehaviours {

  "TrustResidentInUKPage" must {

    beRetrievable[Boolean](TrustResidentInUKPage)

    beSettable[Boolean](TrustResidentInUKPage)

    beRemovable[Boolean](TrustResidentInUKPage)
  }
}
