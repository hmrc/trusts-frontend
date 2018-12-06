package pages

import pages.behaviours.PageBehaviours

class RegisteringTrustFor5APageSpec extends PageBehaviours {

  "RegisteringTrustFor5APage" must {

    beRetrievable[Boolean](RegisteringTrustFor5APage)

    beSettable[Boolean](RegisteringTrustFor5APage)

    beRemovable[Boolean](RegisteringTrustFor5APage)
  }
}
