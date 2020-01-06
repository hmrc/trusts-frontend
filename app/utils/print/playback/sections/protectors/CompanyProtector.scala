package utils.print.playback.sections.protectors

import models.playback.UserAnswers
import pages.register.protectors.company._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import utils.print.playback.sections.AnswerRowConverter
import viewmodels.AnswerSection

object CompanyProtector {

  import AnswerRowConverter._

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Seq[AnswerSection] =
    userAnswers.get(CompanyProtectorNamePage(index)).map { protectorName =>
      Seq(AnswerSection(
        headingKey = Some(messages("answerPage.section.companyProtector.subheading", index + 1)),
        Seq(
          stringQuestion(CompanyProtectorNamePage(index), userAnswers, "companyProtectorName", protectorName),
          addressOrUtrQuestion(CompanyProtectorAddressOrUtrPage(index), userAnswers, "companyProtectorAddressOrUtr", protectorName),
          stringQuestion(CompanyProtectorUtrPage(index), userAnswers, "companyProtectorUtr", protectorName),
          yesNoQuestion(CompanyProtectorAddressUKYesNoPage(index), userAnswers, "companyProtectorAddressUkYesNo", protectorName),
          addressQuestion(CompanyProtectorAddressPage(index), userAnswers, "companyProtectorAddress", protectorName, countryOptions)
        ).flatten,
        None
      ))
    }.getOrElse(Nil)



}
