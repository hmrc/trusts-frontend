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

package queries

import base.SpecBaseHelpers
import models.core.UserAnswers
import models.core.pages.FullName
import pages.behaviours.PageBehaviours
import pages.register.settlors.living_settlor.SettlorIndividualNamePage

class RemoveSettlorQuerySpec extends  PageBehaviours with SpecBaseHelpers {

  "RemoveSettlorQuery" must {

    "remove settlor at index" in {

      val answers: UserAnswers = emptyUserAnswers
      .set(SettlorIndividualNamePage(0), FullName("John", None, "Smith")).success.value
      .set(SettlorIndividualNamePage(1), FullName("Jane", None, "Doe")).success.value

      val result = answers.remove(RemoveSettlorQuery(0)).success.value

      result.get(SettlorIndividualNamePage(0)).value mustBe FullName("Jane", None, "Doe")

      result.get(SettlorIndividualNamePage(1)) mustNot be(defined)
      }

    }

  }

