package pages.register

import pages.QuestionPage
import play.api.libs.json.JsPath

case object IsTrustTaxableYesNoPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "isTrustTaxableYesNo"
}
