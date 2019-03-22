package views

import controllers.routes
import forms.AssetMoneyValueFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.AssetMoneyValueView

class AssetMoneyValueViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "assetMoneyValue"

  val form = new AssetMoneyValueFormProvider()()

  "AssetMoneyValueView view" must {

    val view = viewFor[AssetMoneyValueView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like stringPage(form, applyView, messageKeyPrefix, routes.AssetMoneyValueController.onSubmit(NormalMode).url)
  }
}
