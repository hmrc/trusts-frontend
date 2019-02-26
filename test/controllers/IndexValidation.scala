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

import base.SpecBase
import generators.Generators
import models.UserAnswers
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.prop.PropertyChecks
import pages.QuestionPage
import play.api.http.Writeable
import play.api.libs.json.Writes
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call, Request}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.ErrorTemplate

trait IndexValidation extends SpecBase with PropertyChecks with MockitoSugar with Generators {

  def validateIndex[T](page : Int => QuestionPage[T], route : Call, data : (String, String)*)
                      (implicit gen : Arbitrary[QuestionPage[T]], writes : Writes[T]) = {

    "for a GET" must {

      def getForIndex(index: Int): FakeRequest[AnyContentAsEmpty.type] = {
        val r = route.url

        FakeRequest(GET, r)
      }

      validateIndexHelper(
        page.apply,
        getForIndex
      )(gen.arbitrary, writes, implicitly)

    }

    "for a POST" must {
      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val r = route.url

        FakeRequest(POST, r)
          .withFormUrlEncodedBody(data: _*)
      }

      validateIndexHelper(
        page.apply,
        postForIndex
      )(gen.arbitrary, writes, implicitly)
    }

  }

  private def validateIndexHelper[A, B](
                                createPage: Int => QuestionPage[A],
                                requestForIndex: Int => Request[B]
                              )(implicit generator: Gen[QuestionPage[A]], writes: Writes[A], writeable: Writeable[B]): Unit = {

    "return not found if a given index is out of bounds" in {

      val gen = for {
        answers <- Gen.listOf(generator).map(_.zipWithIndex)
        index <- Gen.oneOf(
          Gen.chooseNum(answers.size + 1, answers.size + 100),
          Gen.chooseNum(-100, -1)
        )
      } yield (answers, index)

      forAll(gen) {
        case (answers, index) =>

          val userAnswers = answers.foldLeft(UserAnswers("id")) {
            case (uA, (answer, i)) =>
              uA.set(generator(i), answer).success.value
          }

          val application =
            applicationBuilder(Some(userAnswers))
              .build()

          val result = route(application, requestForIndex(index)).value

          val view = application.injector.instanceOf[ErrorTemplate]

          val applyView = view.apply(
            messages("global.error.pageNotFound404.title"),
            messages("global.error.pageNotFound404.heading"),
            messages("global.error.pageNotFound404.message")
          )(fakeRequest, messages)

          status(result) mustEqual NOT_FOUND

          contentAsString(result) mustEqual applyView.toString

          application.stop()
      }
    }
  }
}
