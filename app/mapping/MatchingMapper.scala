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

package mapping
import models.{Matched, UserAnswers}
import pages.{ExistingTrustMatched, PostcodeForTheTrustPage, TrustHaveAUTRPage, TrustNamePage, WhatIsTheUTRPage}

class MatchingMapper extends Mapping[MatchData] {

  override def build(userAnswers: UserAnswers): Option[MatchData] = {
    userAnswers.get(TrustHaveAUTRPage).flatMap {
      haveAUTR =>
        if (haveAUTR) {
          userAnswers.get(ExistingTrustMatched).flatMap {
            case Matched.Success =>

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
              None
            case Matched.Failed =>
              None
          }
        } else {
          None
        }
    }
  }

}
