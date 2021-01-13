/*
 * Copyright 2021 HM Revenue & Customs
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
import mapping._
import models.core.{UserAnswers, http}
import models.core.http.Assets
import play.api.Logging

class AssetMapper @Inject()(moneyAssetMapper: MoneyAssetMapper,
                            shareAssetMapper: ShareAssetMapper,
                            propertyOrLandMapper: PropertyOrLandMapper,
                            partnershipAssetMapper: PartnershipAssetMapper,
                            otherAssetMapper: OtherAssetMapper,
                            businessAssetMapper: BusinessAssetMapper
                           ) extends Mapping[Assets] with Logging {

  override def build(userAnswers: UserAnswers): Option[Assets] = {

    val money = moneyAssetMapper.build(userAnswers)
    val shares = shareAssetMapper.build(userAnswers)
    val propertyOrLand = propertyOrLandMapper.build(userAnswers)
    val partnership = partnershipAssetMapper.build(userAnswers)
    val business = businessAssetMapper.build(userAnswers)
    val other = otherAssetMapper.build(userAnswers)

    (money, shares, propertyOrLand, business, partnership, other) match {
      case (None, None, None, None, None, None) =>
        logger.info(s"[build] unable to map assets")
        None
      case _ =>
        Some(
          http.Assets(
            monetary = money,
            propertyOrLand = propertyOrLand,
            shares = shares,
            business = business,
            partnerShip = partnership,
            other = other
          )
        )
    }
  }
}
