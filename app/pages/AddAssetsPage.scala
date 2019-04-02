package pages

import models.AddAssets
import play.api.libs.json.JsPath

case object AddAssetsPage extends QuestionPage[AddAssets] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "addAssets"
}
