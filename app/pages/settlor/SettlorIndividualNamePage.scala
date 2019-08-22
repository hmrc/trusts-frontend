package pages.settlor

import models.FullName
import pages.QuestionPage
import play.api.libs.json.JsPath

final case class SettlorIndividualNamePage(index : Int) extends QuestionPage[FullName] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "settlorIndividualName"
}
