package pages

import models.WhatKindOfAsset
import pages.behaviours.PageBehaviours

class WhatKindOfAssetSpec extends PageBehaviours {

  "WhatKindOfAssetPage" must {

    beRetrievable[WhatKindOfAsset](WhatKindOfAssetPage)

    beSettable[WhatKindOfAsset](WhatKindOfAssetPage)

    beRemovable[WhatKindOfAsset](WhatKindOfAssetPage)
  }
}
