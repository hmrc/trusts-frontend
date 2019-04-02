package pages

import models.AddAssets
import pages.behaviours.PageBehaviours

class AddAssetsSpec extends PageBehaviours {

  "AddAssetsPage" must {

    beRetrievable[AddAssets](AddAssetsPage)

    beSettable[AddAssets](AddAssetsPage)

    beRemovable[AddAssets](AddAssetsPage)
  }
}
