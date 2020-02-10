package pages.register.trustees.individual

import models.core.pages.FullName
import pages.QuestionPage
import play.api.libs.json.JsPath
import sections.Trustees

final case class TrusteesNamePage(index : Int) extends QuestionPage[FullName] {

  override def path: JsPath = Trustees.path \ index \ toString

  override def toString: String = "name"
}
