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

import mapping.Mapping
import models.core.UserAnswers
import models.registration.Matched
import pages.register.trust_details.TrustNamePage
import pages.register.{ExistingTrustMatched, PostcodeForTheTrustPage, TrustHaveAUTRPage, WhatIsTheUTRPage}
import play.api.Logger

class MatchingMapper extends Mapping[MatchData] {

  override def build(userAnswers: UserAnswers): Option[MatchData] = {
    userAnswers.get(TrustHaveAUTRPage).flatMap {
      haveAUTR =>
        if (haveAUTR) {
          userAnswers.get(ExistingTrustMatched).flatMap {
            case Matched.Success =>

              Logger.info(s"[MatchingMapper][build] creating match data for a matched trust")

              for {
                utr <- userAnswers.get(WhatIsTheUTRPage)
                name <- userAnswers.get(TrustNamePage)
                postcode = userAnswers.get(PostcodeForTheTrustPage)
              } yield {
                MatchData(
                  utr = utr,
                  name = name,
                  postCode = postcode
                )
              }

            case Matched.AlreadyRegistered =>
              Logger.info(s"[MatchingMapper][build] unable to create match data as trust is already registered")
              None
            case Matched.Failed =>
              Logger.info(s"[MatchingMapper][build] unable to create match data as trust is failed matching")
              None
          }
        } else {
          None
        }
    }
  }

}
