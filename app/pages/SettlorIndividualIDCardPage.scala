package pages

import models.SettlorIndividualIDCard
import play.api.libs.json.JsPath

case object SettlorIndividualIDCardPage extends QuestionPage[SettlorIndividualIDCard] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "settlorIndividualIDCard"
}
