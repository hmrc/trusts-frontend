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

import javax.inject.Inject
import mapping.Mapping
import mapping.reads.BusinessAsset
import models.core.UserAnswers

class BusinessAssetMapper @Inject()(addressMapper: AddressMapper) extends Mapping[List[BusinessAssetType]] {

  override def build(userAnswers: UserAnswers): Option[List[BusinessAssetType]] = {

    val businesses: List[BusinessAsset] =
      userAnswers.get(mapping.reads.Assets)
        .getOrElse(List.empty[mapping.reads.Asset])
        .collect { case x: BusinessAsset => x }

    businesses match {
      case Nil => None
      case list => Some(
        list.map(x => BusinessAssetType(x.assetName, x.assetDescription, addressMapper.build(x.address), x.currentValue.toLong))
      )
    }
  }
}
