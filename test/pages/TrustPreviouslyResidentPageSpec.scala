package pages

import pages.behaviours.PageBehaviours


class TrustPreviouslyResidentPageSpec extends PageBehaviours {

  "TrustPreviouslyResidentPage" must {

    beRetrievable[String](TrustPreviouslyResidentPage)

    beSettable[String](TrustPreviouslyResidentPage)

    beRemovable[String](TrustPreviouslyResidentPage)
  }
}
