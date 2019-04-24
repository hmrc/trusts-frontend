package pages

import models.AddABeneficiary
import play.api.libs.json.JsPath

case object AddABeneficiaryPage extends QuestionPage[AddABeneficiary] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "addABeneficiary"
}
