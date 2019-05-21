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

import javax.inject.Inject
import models.UserAnswers
import navigation.TaskListNavigator
import viewmodels.{Link, Task}

class RegistrationProgress @Inject()(navigator : TaskListNavigator){

  def sections(userAnswers: UserAnswers) = List(
    Task(Link(TrustDetails, navigator.nextPage(TrustDetails, userAnswers).url), None),
    Task(Link(Settlors, navigator.nextPage(Settlors, userAnswers).url), None),
    Task(Link(Trustees, navigator.nextPage(Trustees, userAnswers).url), None),
    Task(Link(Beneficiaries, navigator.nextPage(Beneficiaries, userAnswers).url), None),
    Task(Link(pages.Assets, navigator.nextPage(pages.Assets, userAnswers).url), None),
    Task(Link(TaxLiability, navigator.nextPage(TaxLiability, userAnswers).url), None)
  )

}
