/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mapping.playback

import com.google.inject.Inject
import mapping.playback.PlaybackExtractionErrors.{FailedToExtractData, PlaybackExtractionError}
import mapping.registration.PassportType
import models.core.pages.{Address, IndividualOrBusiness, InternationalAddress, UKAddress}
import models.playback.http.{DisplayTrustIdentificationOrgType, DisplayTrustIdentificationType, DisplayTrustLeadTrusteeIndType, DisplayTrustLeadTrusteeOrgType, DisplayTrustTrusteeIndividualType, DisplayTrustTrusteeOrgType, DisplayTrustTrusteeType, Trustees}
import models.playback.{MetaData, UserAnswers}
import models.registration.pages.Status.Completed
import pages.entitystatus.TrusteeStatus
import pages.register.trustees._
import play.api.Logger

import scala.util.{Failure, Success, Try}

class TrusteesExtractor @Inject() extends PlaybackExtractor[Option[List[Trustees]]] {

  import PlaybackImplicits._

  override def extract(answers: UserAnswers, data: Option[List[Trustees]]): Either[PlaybackExtractionError, UserAnswers] =
    {
      data match {
        case None => Left(FailedToExtractData("No Trustees"))
        case Some(trustees) =>
          val updated = trustees.zipWithIndex.foldLeft[Try[UserAnswers]](Success(answers)){
            case (answers, (trustee, index)) =>
              trustee match {
                case x : DisplayTrustLeadTrusteeIndType => extractLeadTrusteeIndividual(answers, index, x)
                case x : DisplayTrustLeadTrusteeOrgType => extractLeadTrusteeCompany(answers, index, x)
                case x : DisplayTrustTrusteeOrgType => extractTrusteeCompany(answers, index, x)
                case x : DisplayTrustTrusteeIndividualType => extractTrusteeIndividual(answers, index, x)
                case _ => Failure(new RuntimeException("Unexpected trustee type"))
              }
          }
          updated match {
            case Success(a) => Right(a)
            case Failure(exception) => {
              Logger.warn(s"[TrusteesExtractor] failed to extract data due to ${exception.getMessage}")
              Left(FailedToExtractData(DisplayTrustTrusteeType.toString))
            }
          }
      }
    }

  def extractLeadTrusteeIndividual(answers: Try[UserAnswers], index: Int, leadIndividual : DisplayTrustLeadTrusteeIndType) = {
    answers
      .flatMap(_.set(IsThisLeadTrusteePage(index), true))
      .flatMap(_.set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual))
      .flatMap(_.set(TrusteesNamePage(index), leadIndividual.name.convert))
      .flatMap(_.set(TrusteesDateOfBirthPage(index), leadIndividual.dateOfBirth.convert))
      .flatMap(answers => extractLeadIndividualIdentification(leadIndividual, index, answers))
      .flatMap(_.set(TelephoneNumberPage(index), leadIndividual.phoneNumber))
      .flatMap(_.set(EmailPage(index), leadIndividual.email))
      .flatMap(_.set(TrusteesSafeIdPage(index), leadIndividual.identification.flatMap(_.safeId)))
      .flatMap {
        _.set(
          LeadTrusteeMetaData(index),
          MetaData(
            lineNo = leadIndividual.lineNo,
            bpMatchStatus = leadIndividual.bpMatchStatus,
            entityStart = leadIndividual.entityStart
          )
        )
      }
  }

  def extractLeadTrusteeCompany(answers: Try[UserAnswers], index: Int, leadCompany : DisplayTrustLeadTrusteeOrgType) = {
    answers
      .flatMap(_.set(IsThisLeadTrusteePage(index), true))
      .flatMap(_.set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business))
      .flatMap(_.set(TrusteeOrgNamePage(index), leadCompany.name))
      .flatMap(answers => extractLeadOrgIdentification(leadCompany, index, answers))
      .flatMap(_.set(TelephoneNumberPage(index), leadCompany.phoneNumber))
      .flatMap(_.set(EmailPage(index), leadCompany.email))
      .flatMap(_.set(TrusteesSafeIdPage(index), leadCompany.identification.flatMap(_.safeId)))
      .flatMap {
        _.set(
          LeadTrusteeMetaData(index),
          MetaData(
            lineNo = leadCompany.lineNo,
            bpMatchStatus = leadCompany.bpMatchStatus,
            entityStart = leadCompany.entityStart
          )
        )
      }
  }

  def extractTrusteeIndividual(answers: Try[UserAnswers], index: Int, individual : DisplayTrustTrusteeIndividualType) = {
    answers
      .flatMap(_.set(IsThisLeadTrusteePage(index), false))
      .flatMap(_.set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual))
      .flatMap(_.set(TrusteesNamePage(index), individual.name.convert))
      .flatMap(answers => extractDateOfBirth(individual, index, answers))
      .flatMap(_.set(TelephoneNumberPage(index), individual.phoneNumber))
      .flatMap(answers => extractIndividualIdentification(individual, index, answers))
      .flatMap {
        _.set(
          TrusteeMetaData(index),
          MetaData(
            lineNo = individual.lineNo,
            bpMatchStatus = individual.bpMatchStatus,
            entityStart = individual.entityStart
          )
        )
      }
      .flatMap(_.set(TrusteesSafeIdPage(index), individual.identification.flatMap(_.safeId)))
      .flatMap(_.set(TrusteeStatus(index), Completed))
  }

  def extractTrusteeCompany(answers: Try[UserAnswers], index: Int, company: DisplayTrustTrusteeOrgType) = {
    answers
      .flatMap(_.set(IsThisLeadTrusteePage(index), false))
      .flatMap(_.set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business))
      .flatMap(_.set(TrusteeOrgNamePage(index), company.name))
      .flatMap(answers => extractCompanyIdentification(company, index, answers))
      .flatMap(_.set(TelephoneNumberPage(index), company.phoneNumber))
      .flatMap(_.set(EmailPage(index), company.email))
      .flatMap(_.set(TrusteesSafeIdPage(index), company.identification.flatMap(_.safeId)))
      .flatMap {
        _.set(
          TrusteeMetaData(index),
          MetaData(
            lineNo = company.lineNo,
            bpMatchStatus = company.bpMatchStatus,
            entityStart = company.entityStart
          )
        )
      }
      .flatMap(_.set(TrusteeStatus(index), Completed))
  }

//  private def extractLeadUtr(leadTrustee: DisplayTrustLeadTrusteeOrgType, index: Int, answers: UserAnswers) = {
//    leadTrustee.identification.utr match {
//      case Some(utr) =>
//        answers.set(TrusteeUtrYesNoPage(index), true)
//          .flatMap(_.set(TrusteesUtrPage(index), utr))
//      case None =>
//        // Assumption that user answered no as utr is not provided
//        answers.set(TrusteeUtrYesNoPage(index), false)
//    }
//  }

//  private def extractLeadNino(leadTrustee: DisplayTrustLeadTrusteeIndType, index: Int, answers: UserAnswers) = {
//    leadTrustee.identification.nino match {
//      case Some(nino) =>
//        answers.set(TrusteeAUKCitizenPage(index), true)
//          .flatMap(_.set(TrusteesNinoPage(index), nino))
//      case None =>
//        // Assumption that user answered no as nino is not provided
//        answers.set(TrusteeAUKCitizenPage(index), false)
//    }
//  }

//  private def extractLeadPassportOrIDCard(leadTrustee: DisplayTrustLeadTrusteeIndType, index: Int, answers: UserAnswers) = {
//    leadTrustee.identification.passport match {
//      case Some(passport) =>
//        answers.set(TrusteePassportIDCardPage(index), passport.convert)
//      case None =>
//        Success(answers)
//    }
//  }

//  private def extractLeadIndAddress(leadTrusteeInd: DisplayTrustLeadTrusteeIndType, index: Int, answers: UserAnswers) = {
//    leadTrusteeInd.identification.address.convert match {
//      case Some(uk: UKAddress) =>
//        answers.set(TrusteesUkAddressPage(index), uk)
//          .flatMap(_.set(TrusteeAddressInTheUKPage(index), true))
//      case Some(nonUk: InternationalAddress) =>
//        answers.set(TrusteesInternationalAddressPage(index), nonUk)
//          .flatMap(_.set(TrusteeAddressInTheUKPage(index), false))
//      case None =>
//        Success(answers)
//    }
//  }

//  private def extractLeadOrgAddress(leadTrusteeOrg: DisplayTrustLeadTrusteeOrgType, index: Int, answers: UserAnswers) = {
//    leadTrusteeOrg.identification.address.convert match {
//      case Some(uk: UKAddress) =>
//        answers.set(TrusteesUkAddressPage(index), uk)
//          .flatMap(_.set(TrusteeAddressInTheUKPage(index), true))
//      case Some(nonUk: InternationalAddress) =>
//        answers.set(TrusteesInternationalAddressPage(index), nonUk)
//          .flatMap(_.set(TrusteeAddressInTheUKPage(index), false))
//      case None => Success(answers)
//    }
//  }

  private def extractLeadIndividualIdentification(leadIndividual: DisplayTrustLeadTrusteeIndType, index: Int, answers: UserAnswers) = {
    leadIndividual.identification.map {

      case DisplayTrustIdentificationType(_, Some(nino), None, Some(address)) =>
        answers.set(TrusteeAUKCitizenPage(index), true)
          .flatMap(_.set(TrusteesNinoPage(index), nino))
          .flatMap(answers => extractAddress(address.convert, index, answers))

      case DisplayTrustIdentificationType(_, None, Some(passport), Some(address)) =>
        answers.set(TrusteeAUKCitizenPage(index), false)
          .flatMap(answers => extractPassportIdCard(passport, index, answers))
          .flatMap(answers => extractAddress(address.convert, index, answers))

      case DisplayTrustIdentificationType(_, None, Some(passport), None) =>
        Logger.error(s"[TrusteesExtractor] only passport identification for lead trustee returned in DisplayTrustOrEstate api")
        case object InvalidExtractorState extends RuntimeException
        Failure(InvalidExtractorState)

      case DisplayTrustIdentificationType(_, Some(nino), None, None) =>
        Logger.error(s"[TrusteesExtractor] only nino identification for lead trustee returned in DisplayTrustOrEstate api")
        case object InvalidExtractorState extends RuntimeException
        Failure(InvalidExtractorState)

      case DisplayTrustIdentificationType(_, None, None, Some(address)) =>
        Logger.error(s"[TrusteesExtractor] only address identification for lead trustee returned in DisplayTrustOrEstate api")
        case object InvalidExtractorState extends RuntimeException
        Failure(InvalidExtractorState)

    } getOrElse {
      Logger.error(s"[TrusteesExtractor] no identification for lead trustee returned in DisplayTrustOrEstate api")
      case object InvalidExtractorState extends RuntimeException
      Failure(InvalidExtractorState)
    }
  }


  private def extractIndividualIdentification(individual: DisplayTrustTrusteeIndividualType, index: Int, answers: UserAnswers) = {
    individual.identification map {

      case DisplayTrustIdentificationType(_, Some(nino), None, None) =>
        answers.set(TrusteeNinoYesNoPage(index), true)
          .flatMap(_.set(TrusteesNinoPage(index), nino))

      case DisplayTrustIdentificationType(_, None, Some(passport), None) =>
        Logger.error(s"[TrusteesExtractor] only passport identification returned in DisplayTrustOrEstate api")
        case object InvalidExtractorState extends RuntimeException
        Failure(InvalidExtractorState)

      case DisplayTrustIdentificationType(_, None, None, Some(address)) =>
        answers.set(TrusteeNinoYesNoPage(index), false)
          .flatMap(_.set(TrusteePassportIDCardYesNoPage(index), false))
          .flatMap(answers => extractAddress(address.convert, index, answers))

      case DisplayTrustIdentificationType(_, None, Some(passport), Some(address)) =>
        answers.set(TrusteeNinoYesNoPage(index), false)
          .flatMap(answers => extractAddress(address.convert, index, answers))
          .flatMap(answers => extractPassportIdCard(passport, index, answers))

    } getOrElse {
      answers.set(TrusteeNinoYesNoPage(index), false)
        .flatMap(_.set(TrusteeAddressYesNoPage(index), false))
    }
  }

  private def extractLeadOrgIdentification(leadIndividual: DisplayTrustLeadTrusteeOrgType, index: Int, answers: UserAnswers) = {
    leadIndividual.identification.map {

      case DisplayTrustIdentificationOrgType(_, Some(utr), Some(address)) =>
        answers.set(TrusteeUtrYesNoPage(index), true)
          .flatMap(_.set(TrusteesUtrPage(index), utr))
          .flatMap(answers => extractAddress(address.convert, index, answers))

      case DisplayTrustIdentificationOrgType(_, None, Some(address)) =>
        answers.set(TrusteeUtrYesNoPage(index), false)
          .flatMap(answers => extractAddress(address.convert, index, answers))

      case DisplayTrustIdentificationOrgType(_, Some(utr), None) =>
        Logger.error(s"[TrusteesExtractor] only utr identification for lead trustee company returned in DisplayTrustOrEstate api")
        case object InvalidExtractorState extends RuntimeException
        Failure(InvalidExtractorState)

    } getOrElse {
      Logger.error(s"[TrusteesExtractor] no identification for lead trustee company returned in DisplayTrustOrEstate api")
      case object InvalidExtractorState extends RuntimeException
      Failure(InvalidExtractorState)
    }
  }

  private def extractCompanyIdentification(company: DisplayTrustTrusteeOrgType, index: Int, answers: UserAnswers) = {
    company.identification map {

      case DisplayTrustIdentificationOrgType(_, Some(utr), None) =>
        answers.set(TrusteeUtrYesNoPage(index), true)
          .flatMap(_.set(TrusteesUtrPage(index), utr))

      case DisplayTrustIdentificationOrgType(_, None, Some(address)) =>
        answers.set(TrusteeUtrYesNoPage(index), false)
          .flatMap(answers => extractAddress(address.convert, index, answers))

    } getOrElse {
      answers.set(TrusteeUtrYesNoPage(index), false)
        .flatMap(_.set(TrusteeAddressYesNoPage(index), false))
    }
  }

  private def extractDateOfBirth(trusteeIndividual: DisplayTrustTrusteeIndividualType, index: Int, answers: UserAnswers) = {
    trusteeIndividual.dateOfBirth match {
      case Some(dob) =>
        answers.set(TrusteeDateOfBirthYesNoPage(index), true)
          .flatMap(_.set(TrusteesDateOfBirthPage(index), dob.convert))
      case None =>
        // Assumption that user answered no as utr is not provided
        answers.set(TrusteeDateOfBirthYesNoPage(index), false)
    }
  }

  private def extractPassportIdCard(passport: PassportType, index: Int, answers: UserAnswers) = {
    answers.set(TrusteePassportIDCardYesNoPage(index), true)
      .flatMap(_.set(TrusteePassportIDCardPage(index), passport.convert))
  }

  private def extractAddress(address: Address, index: Int, answers: UserAnswers) = {
    address match {
      case uk: UKAddress =>
        answers.set(TrusteeAddressPage(index), uk)
          .flatMap(_.set(TrusteeAddressYesNoPage(index), true))
          .flatMap(_.set(TrusteeAddressInTheUKPage(index), true))
      case nonUk: InternationalAddress =>
        answers.set(TrusteeAddressPage(index), nonUk)
          .flatMap(_.set(TrusteeAddressYesNoPage(index), true))
          .flatMap(_.set(TrusteeAddressInTheUKPage(index), false))
    }
  }
}