package pages

import models.AddABeneficiary
import pages.behaviours.PageBehaviours

class AddABeneficiarySpec extends PageBehaviours {

  "AddABeneficiaryPage" must {

    beRetrievable[AddABeneficiary](AddABeneficiaryPage)

    beSettable[AddABeneficiary](AddABeneficiaryPage)

    beRemovable[AddABeneficiary](AddABeneficiaryPage)
  }
}
