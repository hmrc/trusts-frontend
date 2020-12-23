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

import base.SpecBaseHelpers
import generators.Generators
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}

class CorrespondenceMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  private val correspondenceMapper: CorrespondenceMapper = injector.instanceOf[CorrespondenceMapper]

  "CorrespondenceMapper" - {

    "must be able to create a correspondence with required answer" - {

      correspondenceMapper.build( "Trust of a Will").name mustBe "Trust of a Will"
    }
  }
}
