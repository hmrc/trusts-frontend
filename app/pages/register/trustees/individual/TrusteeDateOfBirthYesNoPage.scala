package pages.register.trustees.individual

import pages.QuestionPage
import play.api.libs.json.JsPath
import sections.Trustees

final case class  TrusteeDateOfBirthYesNoPage(index : Int) extends QuestionPage[Boolean] {

  override def path: JsPath = Trustees.path \ index \ toString

  override def toString: String = "dateOfBirthYesNo"
}
