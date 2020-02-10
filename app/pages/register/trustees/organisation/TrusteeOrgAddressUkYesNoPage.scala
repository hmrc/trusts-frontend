package pages.register.trustees.organisation

import models.core.UserAnswers
import pages.QuestionPage
import play.api.libs.json.JsPath
import sections.Trustees

import scala.util.Try

final case class  TrusteeOrgAddressUkYesNoPage(index : Int) extends QuestionPage[Boolean] {

  override def path: JsPath = Trustees.path \ index \ toString

  override def toString: String = "addressUKYesNo"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(true) =>
        userAnswers.remove(TrusteeOrgAddressInternationalPage(index))

      case Some(false) =>
        userAnswers.remove(TrusteeOrgAddressUkPage(index))

      case _ => super.cleanup(value, userAnswers)
    }
  }
}
