package pages

import models.IndividualBeneficiaryAddressUK
import play.api.libs.json.JsPath

case object IndividualBeneficiaryAddressUKPage extends QuestionPage[IndividualBeneficiaryAddressUK] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "individualBeneficiaryAddressUK"
}
