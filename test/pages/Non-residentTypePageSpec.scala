package pages

import models.Non-residentType
import pages.behaviours.PageBehaviours

class Non-residentTypeSpec extends PageBehaviours {

  "Non-residentTypePage" must {

    beRetrievable[Non-residentType](Non-residentTypePage)

    beSettable[Non-residentType](Non-residentTypePage)

    beRemovable[Non-residentType](Non-residentTypePage)
  }
}
