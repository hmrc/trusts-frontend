package pages

import pages.behaviours.PageBehaviours

class TrustHaveAUTRPageSpec extends PageBehaviours {

  "TrustHaveAUTRPage" must {

    beRetrievable[Boolean](TrustHaveAUTRPage)

    beSettable[Boolean](TrustHaveAUTRPage)

    beRemovable[Boolean](TrustHaveAUTRPage)
  }
}
