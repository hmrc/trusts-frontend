/*
 * Copyright 2022 HM Revenue & Customs
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

package models

import base.RegistrationSpecBase
import models.registration.pages.TagStatus.{Completed, InProgress, NoActionNeeded, NotStarted}

class TaskStatusesSpec extends RegistrationSpecBase {

  "TaskStatuses" must {

    "return allComplete true" when {

      "all sections completed" in {

        val taskStatuses = TaskStatuses(
          beneficiaries = Completed,
          trustees = Completed,
          taxLiability = Completed,
          protectors = Completed,
          other = Completed,
          trustDetails = Completed,
          settlors = Completed,
          assets = Completed
        )

        taskStatuses.allComplete(taxLiabilityEnabled = true) mustBe true
      }

      "non-taxable and tax liability no action needed" in {

        val taskStatuses = TaskStatuses(
          beneficiaries = Completed,
          trustees = Completed,
          taxLiability = NoActionNeeded,
          protectors = Completed,
          other = Completed,
          trustDetails = Completed,
          settlors = Completed,
          assets = Completed
        )

        taskStatuses.allComplete(taxLiabilityEnabled = false) mustBe true
      }
    }

    "return allComplete false" when {

      "any section incomplete" in {

        val taskStatuses = TaskStatuses(
          beneficiaries = InProgress,
          trustees = Completed,
          taxLiability = Completed,
          protectors = Completed,
          other = Completed,
          trustDetails = Completed,
          settlors = Completed,
          assets = Completed
        )

        taskStatuses.allComplete(taxLiabilityEnabled = true) mustBe false
      }

      "any section not started" in {

        val taskStatuses = TaskStatuses(
          beneficiaries = NotStarted,
          trustees = Completed,
          taxLiability = Completed,
          protectors = Completed,
          other = Completed,
          trustDetails = Completed,
          settlors = Completed,
          assets = Completed
        )

        taskStatuses.allComplete(taxLiabilityEnabled = true) mustBe false
      }

      "trust start date before current tax year start date and tax liability incomplete" in {

        val taskStatuses = TaskStatuses(
          beneficiaries = Completed,
          trustees = Completed,
          taxLiability = InProgress,
          protectors = Completed,
          other = Completed,
          trustDetails = Completed,
          settlors = Completed,
          assets = Completed
        )

        taskStatuses.allComplete(taxLiabilityEnabled = true) mustBe false
      }
    }
  }
}
