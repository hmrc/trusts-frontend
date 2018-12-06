package pages

import pages.behaviours.PageBehaviours

class EstablishedUnderScotsLawPageSpec extends PageBehaviours {

  "EstablishedUnderScotsLawPage" must {

    beRetrievable[Boolean](EstablishedUnderScotsLawPage)

    beSettable[Boolean](EstablishedUnderScotsLawPage)

    beRemovable[Boolean](EstablishedUnderScotsLawPage)
  }
}
