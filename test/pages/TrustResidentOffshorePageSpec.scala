package pages

import pages.behaviours.PageBehaviours

class TrustResidentOffshorePageSpec extends PageBehaviours {

  "TrustResidentOffshorePage" must {

    beRetrievable[Boolean](TrustResidentOffshorePage)

    beSettable[Boolean](TrustResidentOffshorePage)

    beRemovable[Boolean](TrustResidentOffshorePage)
  }
}
