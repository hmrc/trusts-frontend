package pages.register.trustees.individual

import pages.QuestionPage
import play.api.libs.json.JsPath
import sections.Trustees

final case class TrusteesNinoPage(index : Int) extends QuestionPage[String] {

  override def path: JsPath = Trustees.path \ index \ toString

  override def toString: String = "nino"
}
