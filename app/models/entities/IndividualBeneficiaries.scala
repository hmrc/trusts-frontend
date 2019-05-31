package models.entities

import pages.QuestionPage
import play.api.libs.json.JsPath
import viewmodels.addAnother.IndividualBeneficiaryViewModel

case object IndividualBeneficiaries extends QuestionPage[List[IndividualBeneficiary]]{

  override def path: JsPath = JsPath \ Beneficiaries \ toString

  override def toString: String = "individualBeneficiaries"

}
