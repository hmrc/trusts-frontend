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

package viewmodels.addAnother

import models.IndividualOrBusiness.{Business, Individual}
import models.Status.InProgress
import models.{FullName, IndividualOrBusiness, Status}

sealed trait SettlorLivingViewModel extends SettlorViewModel

final case class SettlorLivingIndividualViewModel(`type` : IndividualOrBusiness,
                                                  name : Option[String],
                                                  override val status : Status) extends SettlorLivingViewModel

final case class SettlorLivingBusinessViewModel(`type` : IndividualOrBusiness,
                                                name : Option[String],
                                                override val status : Status) extends SettlorLivingViewModel

final case class SettlorLivingNoNameViewModel(`type` : IndividualOrBusiness,
                                              name : Option[String],
                                              override val status : Status) extends SettlorLivingViewModel

final case class SettlorLivingDefaultViewModel(`type`: IndividualOrBusiness,
                                               override val status: Status) extends SettlorLivingViewModel

object SettlorLivingViewModel {

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit lazy val reads: Reads[SettlorLivingViewModel] = {

    val individualNameReads: Reads[SettlorLivingViewModel] =
    {
      (__ \ "setupAfterSettlorDied").read[Boolean].filter(x => !x).flatMap { _ =>
        (__ \ "individualOrBusiness").read[IndividualOrBusiness].filter(x => x == Individual).flatMap { _ =>
          ((__ \ "name").readNullable[FullName].map(_.map(_.firstName)).filter(x => x.isDefined) and
            (__ \ "status").readWithDefault[Status](InProgress)
            ) ((name, status) => {
            SettlorLivingIndividualViewModel(
              Individual,
              name,
              status)
          })
        }
      }
    }

    val businessNameReads: Reads[SettlorLivingViewModel] =
    {
      (__ \ "setupAfterSettlorDied").read[Boolean].filter(x => !x).flatMap { _ =>
        (__ \ "individualOrBusiness").read[IndividualOrBusiness].filter(x => x == Business).flatMap { _ =>
          ((__ \ "name").readNullable[FullName].map(_.map(_.firstName)).filter(x => x.isDefined) and
            (__ \ "status").readWithDefault[Status](InProgress)
            ) ((name, status) => {
            SettlorLivingBusinessViewModel(
              Business,
              name,
              status)
          })
        }
      }
    }

    val noNameReads : Reads[SettlorLivingViewModel] = {
      (__ \ "setupAfterSettlorDied").read[Boolean].filter(x => !x).flatMap { _ =>
        (__ \ "individualOrBusiness").read[IndividualOrBusiness].flatMap { kind =>
          ((__ \ "name").readNullable[String].filter(x => x.isEmpty) and
            (__ \ "status").readWithDefault[Status](InProgress)
            ) ((name, status) => {
              SettlorLivingNoNameViewModel(kind, name, status)
          })
        }
      }
    }

    val defaultReads : Reads[SettlorLivingViewModel] = {
      (__ \ "setupAfterSettlorDied").read[Boolean].filter(x => !x).flatMap { _ =>
        (__ \ "individualOrBusiness").read[IndividualOrBusiness].flatMap { kind =>
          (__ \ "status").readWithDefault[Status](InProgress).map {
            status =>
              SettlorLivingDefaultViewModel(kind, status)
          }
        }
      }
    }

    individualNameReads orElse businessNameReads orElse noNameReads orElse defaultReads

  }

}