package pages

import pages.behaviours.PageBehaviours

class InheritanceTaxActPageSpec extends PageBehaviours {

  "InheritanceTaxActPage" must {

    beRetrievable[Boolean](InheritanceTaxActPage)

    beSettable[Boolean](InheritanceTaxActPage)

    beRemovable[Boolean](InheritanceTaxActPage)
  }
}
