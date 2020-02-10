package pages.register.trustees.organisation

import models.core.pages.InternationalAddress
import pages.QuestionPage
import play.api.libs.json.JsPath
import sections.Trustees

final case class TrusteeOrgAddressInternationalPage(index: Int) extends QuestionPage[InternationalAddress] {

  override def path: JsPath = Trustees.path \ index \ toString

  override def toString: String = "address"
}
