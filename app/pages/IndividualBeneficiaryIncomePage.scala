package pages

import play.api.libs.json.JsPath

case object IndividualBeneficiaryIncomePage extends QuestionPage[String] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "individualBeneficiaryIncome"
}
