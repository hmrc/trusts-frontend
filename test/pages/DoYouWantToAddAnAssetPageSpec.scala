package pages

import pages.behaviours.PageBehaviours

class DoYouWantToAddAnAssetPageSpec extends PageBehaviours {

  "DoYouWantToAddAnAssetPage" must {

    beRetrievable[Boolean](DoYouWantToAddAnAssetPage)

    beSettable[Boolean](DoYouWantToAddAnAssetPage)

    beRemovable[Boolean](DoYouWantToAddAnAssetPage)
  }
}
