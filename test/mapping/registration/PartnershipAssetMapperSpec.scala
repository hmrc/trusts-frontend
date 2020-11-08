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

import java.time.LocalDate

import base.SpecBaseHelpers
import generators.Generators
import mapping.Mapping
import models.core.http.PartnershipType
import models.registration.pages.Status.Completed
import models.registration.pages.WhatKindOfAsset
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages.entitystatus.AssetStatus
import pages.register.asset.WhatKindOfAssetPage
import pages.register.asset.partnership._

class PartnershipAssetMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  val partnershipAssetMapper: Mapping[List[PartnershipType]] = injector.instanceOf[PartnershipAssetMapper]

  "PartnershipAssetMapper" - {

    "must not be able to create a partnership asset when no description or start date in user answers" in {

      val userAnswers =
        emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Partnership).success.value

      partnershipAssetMapper.build(userAnswers) mustNot be(defined)
    }

    "must able to create a Partnership Asset" in {

      val userAnswers =
        emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Partnership).success.value
          .set(PartnershipDescriptionPage(0), "Partnership Description").success.value
          .set(PartnershipStartDatePage(0), LocalDate.now).success.value
          .set(AssetStatus(0), Completed).success.value

      partnershipAssetMapper.build(userAnswers).value mustBe List(PartnershipType("Partnership Description", LocalDate.now))
    }

    "must able to create multiple Partnership Assets" in {

      val userAnswers =
        emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Partnership).success.value
          .set(PartnershipDescriptionPage(0), "Partnership Description 1").success.value
          .set(PartnershipStartDatePage(0), LocalDate.now).success.value
          .set(AssetStatus(0), Completed).success.value
          .set(WhatKindOfAssetPage(1), WhatKindOfAsset.Partnership).success.value
          .set(PartnershipDescriptionPage(1), "Partnership Description 2").success.value
          .set(PartnershipStartDatePage(1), LocalDate.now).success.value
          .set(AssetStatus(1), Completed).success.value

      partnershipAssetMapper.build(userAnswers).value mustBe List(
        PartnershipType("Partnership Description 1", LocalDate.now),
        PartnershipType("Partnership Description 2", LocalDate.now)
      )

      partnershipAssetMapper.build(userAnswers).value.length mustBe 2
    }
  }
}
