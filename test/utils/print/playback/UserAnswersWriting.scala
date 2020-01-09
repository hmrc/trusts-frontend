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

package utils.print.playback

import cats.data.State
import models.core.pages.{IndividualOrBusiness}
import models.playback.UserAnswers
import org.scalacheck.Arbitrary
import org.scalatest.TryValues
import pages.register.trustees._
import play.api.libs.json.Writes
import queries.Settable
import generators.ModelGenerators

trait UserAnswersWriting extends TryValues with ModelGenerators {
  class SettableWriterOps[T : Writes](s: Settable[T]) {
    def is(value: T): State[UserAnswers, Unit] = writeUA(s, value)
    def :=(value: T): State[UserAnswers, Unit]  = is(value)
    def withArbitraryValue(implicit arb: Arbitrary[T]): State[UserAnswers, Unit] = writeArbUA(s)
    def isRemoved: State[UserAnswers, Unit] = remove(s)
  }

  implicit def settableStuff[T:Writes](s: Settable[T]) : SettableWriterOps[T] = new SettableWriterOps[T](s)

  def writeUA[T](s: Settable[T], value: T)(implicit writes: Writes[T]): State[UserAnswers, Unit] = {
    State(_.set(s, value).success.value -> Unit)
  }

  def writeArbUA[T](s: Settable[T])(implicit writes: Writes[T], arb: Arbitrary[T]): State[UserAnswers, Unit] = {
    arb.arbitrary.sample
      .map(t => writeUA(s, t))
      .getOrElse(throw new Exception(s"Test value generation failure for ${s.path}"))
  }

  def remove(s: Settable[_]): State[UserAnswers, Unit] = {
    State(_.remove(s).success.value -> Unit)
  }

  def individualUKTrustee(index: Int): State[UserAnswers, Unit] = for {
    _ <- IsThisLeadTrusteePage(index) is false
    _ <- TrusteeIndividualOrBusinessPage(index) is IndividualOrBusiness.Individual
    _ <- TrusteesNamePage(index).withArbitraryValue
    _ <- TrusteesDateOfBirthPage(index).withArbitraryValue
    _ <- TrusteeAUKCitizenPage(index) is true
    _ <- TrusteesNinoPage(index) is "AA000000A"
    _ <- TrusteeAddressInTheUKPage(index) is true
    _ <- TrusteesUkAddressPage(index).withArbitraryValue
    _ <- TelephoneNumberPage(index).withArbitraryValue
    _ <- EmailPage(index).withArbitraryValue
  } yield Unit

  def individualNonUkTrustee(index: Int): State[UserAnswers, Unit] = for {
    _ <- individualUKTrustee(index)
    _ <- moveIndividualOutOfUK(index)
  } yield Unit

  def moveIndividualOutOfUK(index: Int): State[UserAnswers, Unit] = for {
    _ <- TrusteeAUKCitizenPage(index) is false
    _ <- TrusteePassportIDCardYesNoPage(index).withArbitraryValue
    _ <- TrusteePassportIDCardPage(index).withArbitraryValue
    _ <- TrusteeAddressInTheUKPage(index) is false
    _ <- TrusteesInternationalAddressPage(index).withArbitraryValue
    _ <- TrusteesNinoPage(index).isRemoved
    _ <- TrusteesUkAddressPage(index).isRemoved
  } yield Unit

  def ukCompanyTrustee(index: Int): State[UserAnswers, Unit] = for {
  _ <- TrusteeIndividualOrBusinessPage(index) is IndividualOrBusiness.Business
  _ <- IsThisLeadTrusteePage(index) is false
  _ <- TrusteeOrgNamePage(index).withArbitraryValue
  _ <- TrusteeUtrYesNoPage(index).withArbitraryValue
  _ <- TrusteesUtrPage(index).withArbitraryValue
  } yield Unit
}
