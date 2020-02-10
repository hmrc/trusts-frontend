package pages.register.trustees.individual

import java.time.LocalDate

import pages.QuestionPage
import play.api.libs.json.JsPath
import sections.Trustees

final case class  TrusteesDateOfBirthPage(index: Int) extends QuestionPage[LocalDate] {

  override def path: JsPath = Trustees.path \ index \ toString

  override def toString: String = "dateOfBirth"
}
