package pages.register.trustees.individual

import models.core.UserAnswers
import pages.QuestionPage
import play.api.libs.json.JsPath
import sections.Trustees

import scala.util.Try

final case class TrusteeAddressInTheUKPage(index : Int) extends QuestionPage[Boolean] {

  override def path: JsPath = Trustees.path \ index \ toString

  override def toString: String = "addressUKYesNo"

  // TODO this is incomplete
  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(false) =>
        userAnswers.remove(TrusteesUkAddressPage(index))
      case _ => super.cleanup(value, userAnswers)
    }
  }
}
