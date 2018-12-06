package pages

import pages.behaviours.PageBehaviours

class AdministrationOutsideUKPageSpec extends PageBehaviours {

  "AdministrationOutsideUKPage" must {

    beRetrievable[Boolean](AdministrationOutsideUKPage)

    beSettable[Boolean](AdministrationOutsideUKPage)

    beRemovable[Boolean](AdministrationOutsideUKPage)
  }
}
