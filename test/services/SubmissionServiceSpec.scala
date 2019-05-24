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

package services

import base.SpecBaseHelpers
import generators.Generators
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}

class SubmissionServiceSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers
{

  val submissionService : SubmissionService = injector.instanceOf[SubmissionService]

  "SubmissionService" -  {

    "for an empty user answers" - {

      "must not be able to create a Registration" in {

        val userAnswers = emptyUserAnswers

        submissionService.submit(userAnswers) mustNot be(defined)
      }

    }

  }

}
