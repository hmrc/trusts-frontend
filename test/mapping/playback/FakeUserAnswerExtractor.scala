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
import mapping.playback.PlaybackExtractionErrors.FailedToExtractData
import mapping.playback.beneficiaries.BeneficiaryExtractor
import mapping.playback.settlors.{SettlorExtractor, TrustTypeExtractor}
import models.playback.UserAnswers
import models.playback.http.GetTrust

class FakeUserAnswerExtractor @Inject()(beneficiary: BeneficiaryExtractor,
                                        leadTrustee: LeadTrusteeExtractor,
                                        settlors: SettlorExtractor,
                                        trustType: TrustTypeExtractor,
                                        trustees: TrusteeExtractor
                                        ) extends UserAnswersExtractor(
  beneficiary, leadTrustee, settlors, trustType, trustees
) {

  override def extract(answers: UserAnswers, data: GetTrust): Either[PlaybackExtractionErrors.PlaybackExtractionError, UserAnswers] =
    Right(UserAnswers("id"))

}


class FakeFailingUserAnswerExtractor @Inject()(beneficiary: BeneficiaryExtractor,
                                               leadTrustee: LeadTrusteeExtractor,
                                               settlors: SettlorExtractor,
                                               trustType: TrustTypeExtractor,
                                               trustees: TrusteeExtractor
                                       ) extends UserAnswersExtractor(
  beneficiary, leadTrustee, settlors, trustType, trustees
) {

  override def extract(answers: UserAnswers, data: GetTrust): Either[PlaybackExtractionErrors.PlaybackExtractionError, UserAnswers] =
    Left(FailedToExtractData("No beneficiaries"))

}
