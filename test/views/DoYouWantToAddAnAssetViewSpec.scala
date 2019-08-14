package views

import controllers.routes
import forms.DoYouWantToAddAnAssetFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.DoYouWantToAddAnAssetView

class DoYouWantToAddAnAssetViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "doYouWantToAddAnAsset"

  val form = new DoYouWantToAddAnAssetFormProvider()()

  "DoYouWantToAddAnAsset view" must {

    val view = viewFor[DoYouWantToAddAnAssetView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.DoYouWantToAddAnAssetController.onSubmit(NormalMode, fakeDraftId).url)
  }
}
