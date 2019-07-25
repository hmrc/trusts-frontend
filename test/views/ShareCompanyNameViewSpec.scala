package views

import controllers.routes
import forms.ShareCompanyNameFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.ShareCompanyNameView

class ShareCompanyNameViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "shareCompanyName"

  val form = new ShareCompanyNameFormProvider()()

  "ShareCompanyNameView view" must {

    val view = viewFor[ShareCompanyNameView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like stringPage(form, applyView, messageKeyPrefix, routes.ShareCompanyNameController.onSubmit(NormalMode).url)
  }
}
