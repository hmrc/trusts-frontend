package pages.register.settlors

import models.core.UserAnswers
import pages.QuestionPage
import pages.register.settlors.living_settlor.trust_type.{HoldoverReliefYesNoPage, KindOfTrustPage}
import play.api.libs.json.JsPath
import sections.{DeceasedSettlor, LivingSettlors, Settlors}

import scala.util.Try

case object SetUpAfterSettlorDiedYesNoPage extends QuestionPage[Boolean] {

  override def path: JsPath = Settlors.path \toString

  override def toString: String = "setupAfterSettlorDiedYesNo"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(false) =>
        userAnswers.remove(DeceasedSettlor)
      case Some(true) =>
        userAnswers.remove(KindOfTrustPage)
          .flatMap(_.remove(HoldoverReliefYesNoPage))
          .flatMap(_.remove(LivingSettlors))
      case _ => super.cleanup(value, userAnswers)
    }
  }
}
