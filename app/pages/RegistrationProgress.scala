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

package pages

import controllers.routes
import models.{NormalMode, UserAnswers}
import viewmodels.{Completed, InProgress, Link, Task}

object RegistrationProgress {

  def sections(userAnswers: UserAnswers) = List(
    Task(Link(TrustDetails, routes.TrustNameController.onPageLoad(NormalMode).url), None),
    Task(Link(Settlors, routes.TaskListController.onPageLoad().url), Some(InProgress)),
    Task(Link(Trustees, routes.IsThisLeadTrusteeController.onPageLoad().url), Some(InProgress)),
    Task(Link(Beneficiaries, routes.AddATrusteeController.onPageLoad().url), None),
    Task(Link(pages.Assets, routes.AddATrusteeController.onPageLoad().url), None),
    Task(Link(TaxLiability, routes.AddATrusteeController.onPageLoad().url), Some(Completed))
  )

}
