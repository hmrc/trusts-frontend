package pages.register

import models.core.UserAnswers
import models.registration.pages.TrusteesBasedInTheUK
import models.registration.pages.TrusteesBasedInTheUK.{InternationalAndUKTrustees, NonUkBasedTrustees, UKBasedTrustees}
import pages.QuestionPage
import pages.entitystatus.TrustDetailsStatus
import pages.register.agents.AgentOtherThanBarristerPage
import pages.register.settlors.SettlorsBasedInTheUKPage
import play.api.libs.json.JsPath
import sections.TrustDetails

import scala.util.Try

case object TrusteesBasedInTheUKPage extends QuestionPage[TrusteesBasedInTheUK] {

  override def path: JsPath = JsPath \ TrustDetails \ toString

  override def toString: String = "trusteesBasedInTheUK"

  override def cleanup(value: Option[TrusteesBasedInTheUK], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(UKBasedTrustees) | Some(InternationalAndUKTrustees) =>
        userAnswers.remove(EstablishedUnderScotsLawPage)
          .flatMap(_.remove(TrustResidentOffshorePage))
          .flatMap(_.remove(TrustPreviouslyResidentPage))
          .flatMap(_.remove(TrustDetailsStatus))
          .flatMap(_.remove(SettlorsBasedInTheUKPage))
      case Some(NonUkBasedTrustees) | Some(InternationalAndUKTrustees) =>
        userAnswers.remove(RegisteringTrustFor5APage)
          .flatMap(_.remove(InheritanceTaxActPage))
          .flatMap(_.remove(NonResidentTypePage))
          .flatMap(_.remove(AgentOtherThanBarristerPage))
          .flatMap(_.remove(TrustDetailsStatus))
          .flatMap(_.remove(SettlorsBasedInTheUKPage))
      case _ =>
        super.cleanup(value, userAnswers)
    }
  }
}
