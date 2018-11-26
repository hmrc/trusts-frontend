package pages

import pages.behaviours.PageBehaviours


class TrustSettledDatePageSpec extends PageBehaviours {

  "TrustSettledDatePage" must {

    beRetrievable[String](TrustSettledDatePage)

    beSettable[String](TrustSettledDatePage)

    beRemovable[String](TrustSettledDatePage)
  }
}
