package pages

import play.api.libs.json.JsPath

case object AdministrationOutsideUKPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "administrationOutsideUK"
}
