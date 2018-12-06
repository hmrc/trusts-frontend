package pages

import pages.behaviours.PageBehaviours


class CountryGoverningTrustPageSpec extends PageBehaviours {

  "CountryGoverningTrustPage" must {

    beRetrievable[String](CountryGoverningTrustPage)

    beSettable[String](CountryGoverningTrustPage)

    beRemovable[String](CountryGoverningTrustPage)
  }
}
