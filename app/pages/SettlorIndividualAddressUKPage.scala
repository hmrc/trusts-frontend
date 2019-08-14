package pages

import models.SettlorIndividualAddressUK
import play.api.libs.json.JsPath

case object SettlorIndividualAddressUKPage extends QuestionPage[SettlorIndividualAddressUK] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "settlorIndividualAddressUK"
}
