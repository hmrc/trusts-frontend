package pages

import pages.behaviours.PageBehaviours


class AgencysTelehponeNumberPageSpec extends PageBehaviours {

  "AgencysTelehponeNumberPage" must {

    beRetrievable[String](AgencysTelehponeNumberPage)

    beSettable[String](AgencysTelehponeNumberPage)

    beRemovable[String](AgencysTelehponeNumberPage)
  }
}
