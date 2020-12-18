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

package models

import base.RegistrationSpecBase
import models.RegistrationSubmission.AllStatus
import models.registration.pages.Status.Completed
import uk.gov.hmrc.auth.core.AffinityGroup._

class SubmissionDraftResponseSpec extends RegistrationSpecBase {

  "SubmissionDraftResponse" when {

    "AllStatus" must {

      "return allComplete true" when {
        "all sections completed" in {

          val allStatus = AllStatus(
            beneficiaries = Some(Completed),
            trustees = Some(Completed),
            taxLiability = Some(Completed),
            protectors = Some(Completed),
            otherIndividuals = Some(Completed),
            trustDetails = Some(Completed),
            settlors = Some(Completed),
            assets = Some(Completed)
          )

          allStatus.allComplete(None, Organisation) mustBe true
        }
      }

      "return allComplete false" when {
        "any section incomplete" in {

          val allStatus = AllStatus(
            beneficiaries = None,
            trustees = Some(Completed),
            taxLiability = Some(Completed),
            protectors = Some(Completed),
            otherIndividuals = Some(Completed),
            trustDetails = Some(Completed),
            settlors = Some(Completed),
            assets = Some(Completed)
          )

          allStatus.allComplete(None, Organisation) mustBe false
        }
      }
    }
  }
}
