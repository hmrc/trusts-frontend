package pages

import pages.behaviours.PageBehaviours


class ShareCompanyNamePageSpec extends PageBehaviours {

  "ShareCompanyNamePage" must {

    beRetrievable[String](ShareCompanyNamePage)

    beSettable[String](ShareCompanyNamePage)

    beRemovable[String](ShareCompanyNamePage)
  }
}
