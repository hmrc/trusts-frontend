package pages

import play.api.libs.json.JsPath

case object IndividualBeneficiaryVulnerableYesNoPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "individualBeneficiaryVulnerableYesNo"
}
