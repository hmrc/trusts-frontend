package pages

import pages.behaviours.PageBehaviours


class AssetMoneyValuePageSpec extends PageBehaviours {

  "AssetMoneyValuePage" must {

    beRetrievable[String](AssetMoneyValuePage)

    beSettable[String](AssetMoneyValuePage)

    beRemovable[String](AssetMoneyValuePage)
  }
}
