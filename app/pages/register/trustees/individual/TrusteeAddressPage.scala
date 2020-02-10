package pages.register.trustees.individual

import models.core.pages.Address
import pages.QuestionPage
import play.api.libs.json.JsPath
import sections.Trustees

final case class TrusteeAddressPage(index: Int) extends QuestionPage[Address] {

  override def path: JsPath = Trustees.path \ index \ toString

  override def toString: String = "address"
}
