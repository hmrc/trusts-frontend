package pages.register.trustees.organisation

import models.core.pages.UKAddress
import pages.QuestionPage
import play.api.libs.json.JsPath
import sections.Trustees

final case class TrusteeOrgAddressUkPage(index: Int) extends QuestionPage[UKAddress] {

  override def path: JsPath = Trustees.path \ index \ toString

  override def toString: String = "address"
}
