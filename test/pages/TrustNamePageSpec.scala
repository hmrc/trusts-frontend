package pages

import pages.behaviours.PageBehaviours


class TrustNamePageSpec extends PageBehaviours {

  "TrustNamePage" must {

    beRetrievable[String](TrustNamePage)

    beSettable[String](TrustNamePage)

    beRemovable[String](TrustNamePage)
  }
}
