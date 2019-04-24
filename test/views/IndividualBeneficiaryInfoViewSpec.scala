package views

import views.behaviours.ViewBehaviours
import views.html.IndividualBeneficiaryInfoView

class IndividualBeneficiaryInfoViewSpec extends ViewBehaviours {

  "IndividualBeneficiaryInfo view" must {

    val view = viewFor[IndividualBeneficiaryInfoView](Some(emptyUserAnswers))

    val applyView = view.apply()(fakeRequest, messages)

    behave like normalPage(applyView, "individualBeneficiaryInfo")

    behave like pageWithBackLink(applyView)
  }
}
