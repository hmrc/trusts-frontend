package pages

import models.WhatKindOfAsset
import play.api.libs.json.JsPath

case object WhatKindOfAssetPage extends QuestionPage[WhatKindOfAsset] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "whatKindOfAsset"
}
