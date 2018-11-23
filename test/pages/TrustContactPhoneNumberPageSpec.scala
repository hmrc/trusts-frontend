package pages

import pages.behaviours.PageBehaviours


class TrustContactPhoneNumberPageSpec extends PageBehaviours {

  "TrustContactPhoneNumberPage" must {

    beRetrievable[String](TrustContactPhoneNumberPage)

    beSettable[String](TrustContactPhoneNumberPage)

    beRemovable[String](TrustContactPhoneNumberPage)
  }
}
