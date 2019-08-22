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

package controllers

import play.api.mvc.Call
import queries.{RemoveAssetQuery, Settable}


trait RemoveAssetController extends RemoveIndexController {

  override def redirect(draftId : String) : Call =
    controllers.routes.AddAssetsController.onPageLoad(draftId)

  override def removeQuery(index: Int): Settable[_] = RemoveAssetQuery(index)
}

