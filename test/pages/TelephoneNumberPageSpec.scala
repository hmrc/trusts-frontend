package pages

import pages.behaviours.PageBehaviours


class TelephoneNumberPageSpec extends PageBehaviours {

  "TelephoneNumberPage" must {

    beRetrievable[String](TelephoneNumberPage)

    beSettable[String](TelephoneNumberPage)

    beRemovable[String](TelephoneNumberPage)
  }
}
