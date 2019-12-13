/*
 * Copyright 2019 HM Revenue & Customs
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

package mapping.playback.beneficiaries

import com.google.inject.Inject
import mapping.playback.PlaybackExtractionErrors.{FailedToExtractData, PlaybackExtractionError}
import mapping.playback.PlaybackExtractor
import models.playback.UserAnswers
import models.playback.http.DisplayTrustBeneficiaryType

class BeneficiaryExtractor @Inject()(charityBeneficiaryExtractor: CharityBeneficiaryExtractor,
                                     companyBeneficiaryExtractor: CompanyBeneficiaryExtractor,
                                     trustBeneficiaryExtractor: TrustBeneficiaryExtractor,
                                     otherBeneficiaryExtractor: OtherBeneficiaryExtractor) extends PlaybackExtractor[DisplayTrustBeneficiaryType] {

  override def extract(answers: UserAnswers, data: DisplayTrustBeneficiaryType): Either[PlaybackExtractionError, UserAnswers] = {

    import models.playback.UserAnswersCombinator._

    val beneficiaries: List[UserAnswers] = List(
      charityBeneficiaryExtractor.extract(answers, data.charity),
      companyBeneficiaryExtractor.extract(answers, data.company),
      trustBeneficiaryExtractor.extract(answers, data.trust),
      otherBeneficiaryExtractor.extract(answers, data.other)
    ).collect {
      case Right(z) => z
    }

    beneficiaries match {
      case Nil => Left(FailedToExtractData("Beneficiary Extraction Error"))
      case _ => beneficiaries.combine.map(Right.apply).getOrElse(Left(FailedToExtractData("Beneficiary Extraction Error")))
    }
  }
}