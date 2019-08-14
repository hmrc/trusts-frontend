package pages

import play.api.libs.json.JsPath

case object SettlorIndividualPassportYesNoPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "settlorIndividualPassportYesNo"
}
