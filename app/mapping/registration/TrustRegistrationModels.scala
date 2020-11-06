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

package mapping.registration

import java.time.LocalDate

import models.core.pages.FullName
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Trust Registration API Schema - definitions models below
  */

case class Registration(matchData: Option[MatchData],
                        correspondence: Correspondence,
                        declaration: Declaration,
                        trust: Trust,
                        agentDetails: Option[AgentDetails] = None
                       )

object Registration {
 implicit val registrationReads : Format[Registration] = Json.format[Registration]
}

case class Details(trust: Trust)

object Details {
  implicit val detailsFormat: Format[Details] = Json.format[Details]
}

case class MatchData(utr: String,
                     name: String,
                     postCode: Option[String]
                    )

object MatchData {
  implicit val matchDataFormat: Format[MatchData] = Json.format[MatchData]

  val writes: Writes[MatchData] =
    ((__ \ "utr").write[String] and
      (__ \ "name").write[String] and
      (__ \ "postcode").writeNullable[String]
      ).apply(unlift(MatchData.unapply))
}

case class Correspondence(name: String)

object Correspondence {
  implicit val correspondenceFormat : Format[Correspondence] = Json.format[Correspondence]
}

case class Assets(monetary: Option[List[AssetMonetaryAmount]],
                  propertyOrLand: Option[List[PropertyLandType]],
                  shares: Option[List[SharesType]],
                  business: Option[List[BusinessAssetType]],
                  partnerShip: Option[List[PartnershipType]],
                  other: Option[List[OtherAssetType]])

object Assets {
  implicit val assetsFormat: Format[Assets] = Json.format[Assets]
}

case class AssetMonetaryAmount(assetMonetaryAmount: Long)

object AssetMonetaryAmount {
  implicit val assetMonetaryAmountFormat: Format[AssetMonetaryAmount] = Json.format[AssetMonetaryAmount]
}

case class Declaration(name: FullName,
                       address: AddressType)

object Declaration {
  implicit val declarationFormat: Format[Declaration] = Json.format[Declaration]
}

case class Trust(assets: Assets)

object Trust {
  implicit val trustFormat: Format[Trust] = Json.format[Trust]
}

case class LeadTrusteeIndType (
                            name: FullName,
                            dateOfBirth: LocalDate ,
                            phoneNumber: String,
                            email: Option[String] = None,
                            identification: IdentificationType
                          )

object LeadTrusteeIndType {
  implicit val leadTrusteeIndTypeFormat: Format[LeadTrusteeIndType] = Json.format[LeadTrusteeIndType]
}

case class LeadTrusteeOrgType(
                               name: String,
                               phoneNumber: String,
                               email: Option[String] = None,
                               identification: IdentificationOrgType
                             )

object LeadTrusteeOrgType {
  implicit val leadTrusteeOrgTypeFormat: Format[LeadTrusteeOrgType] = Json.format[LeadTrusteeOrgType]
}

case class LeadTrusteeType(
                            leadTrusteeInd : Option[LeadTrusteeIndType] = None,
                            leadTrusteeOrg : Option[LeadTrusteeOrgType] = None
                          )

object LeadTrusteeType {
  implicit val leadTrusteeTypeReads:Format[LeadTrusteeType] = Json.format[LeadTrusteeType]
}

case class IdentificationOrgType(utr: Option[String],
                                 address: Option[AddressType])

object IdentificationOrgType {
  implicit val trustBeneficiaryIdentificationFormat: Format[IdentificationOrgType] = Json.format[IdentificationOrgType]
}

case class IdentificationType(nino: Option[String],
                              passport: Option[PassportType],
                              address: Option[AddressType])

object IdentificationType {
  implicit val identificationTypeFormat: Format[IdentificationType] = Json.format[IdentificationType]
}

case class PropertyLandType(buildingLandName: Option[String],
                            address: Option[AddressType],
                            valueFull: Long,
                            valuePrevious: Long)

object PropertyLandType {
  implicit val propertyLandTypeFormat: Format[PropertyLandType] = Json.format[PropertyLandType]
}

case class BusinessAssetType(orgName: String,
                             businessDescription: String,
                             address: AddressType,
                             businessValue: Long)

object BusinessAssetType {
  implicit val businessAssetTypeFormat: Format[BusinessAssetType] = Json.format[BusinessAssetType]
}

case class OtherAssetType(description: String,
                          value: Long)

object OtherAssetType {
  implicit val otherAssetTypeFormat: Format[OtherAssetType] = Json.format[OtherAssetType]
}

case class PartnershipType(description: String,
                           partnershipStart: LocalDate)

object PartnershipType {
  implicit val partnershipTypeFormat: Format[PartnershipType] = Json.format[PartnershipType]
}

case class SharesType(numberOfShares: String,
                      orgName: String,
                      shareClass: String,
                      typeOfShare: String,
                      value: Long)

object SharesType {
  implicit val sharesTypeFormat: Format[SharesType] = Json.format[SharesType]
}

case class AddressType(line1: String,
                       line2: String,
                       line3: Option[String],
                       line4: Option[String],
                       postCode: Option[String],
                       country: String) {
  def isInUk: Boolean = country == "GB"
}

object AddressType {
  implicit val addressTypeFormat: Format[AddressType] = Json.format[AddressType]
}

case class PassportType(number: String,
                        expirationDate: LocalDate,
                        countryOfIssue: String)

object PassportType {
  implicit val passportTypeFormat: Format[PassportType] = Json.format[PassportType]
}

case class AgentDetails(arn: String,
                        agentName: String,
                        agentAddress: AddressType,
                        agentTelephoneNumber: String,
                        clientReference: String)

object AgentDetails {
  implicit val agentDetailsFormat: Format[AgentDetails] = Json.format[AgentDetails]
}
