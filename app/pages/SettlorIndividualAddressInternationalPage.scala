package pages

import models.SettlorIndividualAddressInternational
import play.api.libs.json.JsPath

case object SettlorIndividualAddressInternationalPage extends QuestionPage[SettlorIndividualAddressInternational] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "settlorIndividualAddressInternational"
}
