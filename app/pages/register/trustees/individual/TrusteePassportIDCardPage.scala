package pages.register.trustees.individual

import models.registration.pages.PassportOrIdCardDetails
import pages.QuestionPage
import play.api.libs.json.JsPath
import sections.Trustees

final case class TrusteePassportIDCardPage(index : Int) extends QuestionPage[PassportOrIdCardDetails] {

  override def path: JsPath = Trustees.path \ index \ toString

  override def toString: String = "passportIdCard"
}
