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

package controllers.register

import base.RegistrationSpecBase
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.register.{RegistrationProgress, RegistrationSubmissionDatePage, RegistrationTRNPage}
import play.api.inject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestUserAnswers
import viewmodels.{AnswerSection, RegistrationAnswerSections}
import views.html.register.ConfirmationAnswerPageView

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.concurrent.Future

class ConfirmationAnswerPageControllerSpec extends RegistrationSpecBase {

  val index = 0

  "ConfirmationAnswerPageController" must {

    val beneficiarySections = List(
      AnswerSection(
        Some("beneficiaryHeadingKey1"),
        List.empty,
        Some("beneficiarySectionKey1")
      ),
      AnswerSection(
        Some("beneficiaryHeadingKey2"),
        List.empty,
        Some("beneficiarySectionKey2")
      )
    )

    val trusteeSections = List(
      AnswerSection(
        Some("trusteeHeadingKey1"),
        List.empty,
        Some("trusteeSectionKey1")
      ),
      AnswerSection(
        Some("trusteeHeadingKey2"),
        List.empty,
        Some("trusteeSectionKey2")
      )
    )

    val trustDetailsSection = List(
      AnswerSection(
        Some("trustDetailsHeadingKey1"),
        List.empty,
        Some("trustDetailsSectionKey1")
      )
    )

    val settlorsSection = List(
      AnswerSection(
        Some("settlorsHeadingKey1"),
        List.empty,
        Some("settlorsSectionKey1")
      )
    )

    val assetsSection = List(
      AnswerSection(
        Some("assetsHeadingKey1"),
        List.empty,
        Some("assetsSectionKey1")
      )
    )

    val registrationSections = RegistrationAnswerSections(
      beneficiaries = Some(beneficiarySections),
      trustees = Some(trusteeSections),
      trustDetails = Some(trustDetailsSection),
      settlors = Some(settlorsSection),
      assets = Some(assetsSection)
    )

    when(mockCreateDraftRegistrationService.getAnswerSections(any())(any(), any()))
      .thenReturn(Future.successful(registrationSections))

    "return OK and the correct view for a GET when tasklist completed" in {

      val userAnswers = TestUserAnswers.emptyUserAnswers
        .set(RegistrationTRNPage, "XNTRN000000001").success.value
        .set(RegistrationSubmissionDatePage, LocalDateTime.now).success.value

      val expectedSections = Seq(
        trustDetailsSection.head,
        trusteeSections.head,
        trusteeSections(1),
        beneficiarySections.head,
        beneficiarySections(1),
        settlorsSection.head,
        assetsSection.head
      )

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .build()

      val request = FakeRequest(GET, routes.ConfirmationAnswerPageController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ConfirmationAnswerPageView]

      status(result) mustEqual OK

      val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
      val trnDateTime = LocalDateTime.now.format(dateFormatter)

      contentAsString(result) mustEqual
        view(expectedSections, "XNTRN000000001", trnDateTime, isTaxable = true)(request, messages).toString

      application.stop()
    }

    "redirect to tasklist page when tasklist not completed" in {

      val userAnswers =
        TestUserAnswers.emptyUserAnswers

      val mockRegistrationProgress = mock[RegistrationProgress]

      when(mockRegistrationProgress.isTaskListComplete(any(), any(), any(), any())(any()))
        .thenReturn(Future.successful(false))

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(inject.bind[RegistrationProgress].toInstance(mockRegistrationProgress))
        .build()

      val request = FakeRequest(GET, routes.ConfirmationAnswerPageController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.TaskListController.onPageLoad(fakeDraftId).url

      application.stop()

    }
  }
}
