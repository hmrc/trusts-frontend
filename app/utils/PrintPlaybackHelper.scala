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

package utils

import controllers.actions.playback.PlaybackDataRequest
import javax.inject.Inject
import mapping.playback.UserAnswersExtractor
import models.playback.http.GetTrust
import play.api.i18n.Messages
import play.api.mvc.AnyContent
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection

class PrintPlaybackHelper @Inject()(countryOptions: CountryOptions,
                                    userAnswersExtractor: UserAnswersExtractor){

  def summary(userAnswers: models.playback.UserAnswers)(implicit messages: Messages, request: PlaybackDataRequest[AnyContent]) : Seq[AnswerSection] = {

    val playbackAnswersHelper: PlaybackAnswersHelper = new PlaybackAnswersHelper(countryOptions)(userAnswers)

    val entitySections = List(
      playbackAnswersHelper.deceasedSettlor,
      playbackAnswersHelper.charityBeneficiary(0)
    ).flatten.flatten

    List(entitySections).flatten
  }
}
