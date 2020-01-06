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

package mapping.playback.protectors

import com.google.inject.Inject
import mapping.playback.PlaybackExtractionErrors.{FailedToExtractData, PlaybackExtractionError}
import mapping.playback.PlaybackExtractor
import models.playback.UserAnswers
import models.playback.http.DisplayTrustProtectorsType
import pages.register.protectors.DoesTrustHaveAProtectorYesNoPage

import scala.util.Success

class ProtectorExtractor @Inject()(individualProtectorExtractor: IndividualProtectorExtractor,
                                   companyProtectorExtractor: CompanyProtectorExtractor) extends PlaybackExtractor[Option[DisplayTrustProtectorsType]] {

  override def extract(answers: UserAnswers, data: Option[DisplayTrustProtectorsType]): Either[PlaybackExtractionError, UserAnswers] = {

    import models.playback.UserAnswersCombinator._

    val protectors = List(
      data.flatMap(_.protector),
      data.flatMap(_.protectorCompany)
    ).collect {
      case Some(z) => z
    }

    val updatedAnswers: UserAnswers = updateAnswers(answers, doesTrustHaveProtector = protectors.nonEmpty)

    data match {
      case Some(p) =>
        List(
          individualProtectorExtractor.extract(updatedAnswers, p.protector),
          companyProtectorExtractor.extract(updatedAnswers, p.protectorCompany)
        ).collect {
          case Right(z) => z
        }.combine.map(Right.apply).getOrElse(Left(FailedToExtractData("Protector Extraction Error")))
      case None =>
        Right(updatedAnswers)
    }

  }

  def updateAnswers(answers: UserAnswers, doesTrustHaveProtector: Boolean): UserAnswers = {
    answers.set(DoesTrustHaveAProtectorYesNoPage(), doesTrustHaveProtector) match {
      case Success(ua) => ua
      case _ => answers
    }
  }
}