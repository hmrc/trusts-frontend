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

package utils.print.playback

import javax.inject.Inject
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import utils.print.playback.sections.DeceasedSettlor
import viewmodels.AnswerSection

class PrintPlaybackHelper @Inject()(countryOptions: CountryOptions){

  def summary(userAnswers: models.playback.UserAnswers)(implicit messages: Messages) : Seq[AnswerSection] = {

    val playbackAnswersHelper: PlaybackAnswersHelper = new PlaybackAnswersHelper(countryOptions, userAnswers)

    List(
      playbackAnswersHelper.allTrustees,
      DeceasedSettlor(userAnswers, countryOptions),
      playbackAnswersHelper.settlors,
      playbackAnswersHelper.beneficiaries,
      playbackAnswersHelper.protectors,
      playbackAnswersHelper.otherIndividual,
      // trust type must go last
      playbackAnswersHelper.trustType
    ).flatten

  }
}
