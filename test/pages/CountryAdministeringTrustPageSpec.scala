package pages

import pages.behaviours.PageBehaviours


class CountryAdministeringTrustPageSpec extends PageBehaviours {

  "CountryAdministeringTrustPage" must {

    beRetrievable[String](CountryAdministeringTrustPage)

    beSettable[String](CountryAdministeringTrustPage)

    beRemovable[String](CountryAdministeringTrustPage)
  }
}
