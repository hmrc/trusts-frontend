package pages

import pages.behaviours.PageBehaviours
import models.FullName

class TrusteesNamePageSpec extends PageBehaviours {

  "TrusteesNamePage" must {

      beRetrievable[FullName](TrusteesNamePage)

      beSettable[FullName](TrusteesNamePage)

      beRemovable[FullName](TrusteesNamePage)
    }
}
