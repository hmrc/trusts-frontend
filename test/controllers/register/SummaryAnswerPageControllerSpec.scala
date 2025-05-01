/*
 * Copyright 2024 HM Revenue & Customs
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
import org.mockito.ArgumentMatchers.any
import pages.register.RegistrationProgress
import play.api.inject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import utils.TestUserAnswers
import viewmodels.{AnswerSection, RegistrationAnswerSections}
import views.html.register.SummaryAnswerPageView
import org.mockito.Mockito.when

import scala.concurrent.Future

class SummaryAnswerPageControllerSpec extends RegistrationSpecBase {

  val trustDetailsSection: Seq[AnswerSection] = Seq(
    AnswerSection(
      Some("trustDetailsHeadingKey1"),
      List.empty,
      Some("trustDetailsSectionKey1")
    )
  )

  val beneficiarySections: Seq[AnswerSection] = Seq(
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

  val trusteeSections: Seq[AnswerSection] = Seq(
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

  val protectorSections: Seq[AnswerSection] = Seq(
    AnswerSection(
      Some("protectorHeadingKey1"),
      List.empty,
      Some("protectorSectionKey1")
    ),
    AnswerSection(
      Some("protectorHeadingKey2"),
      List.empty,
      Some("protectorSectionKey2")
    )
  )

  val otherIndividualSections: Seq[AnswerSection] = Seq(
    AnswerSection(
      Some("otherIndividualHeadingKey1"),
      List.empty,
      Some("otherIndividualSectionKey1")
    ),
    AnswerSection(
      Some("otherIndividualHeadingKey2"),
      List.empty,
      Some("otherIndividualSectionKey2")
    )
  )

  val assetSections: Seq[AnswerSection] = Seq(
    AnswerSection(
      Some("assetHeadingKey1"),
      List.empty,
      Some("assetSectionKey1")
    ),
    AnswerSection(
      Some("assetHeadingKey2"),
      List.empty,
      Some("assetSectionKey2")
    )
  )

  val registrationSections: RegistrationAnswerSections = RegistrationAnswerSections(
    beneficiaries = Some(beneficiarySections.toList),
    trustees = Some(trusteeSections.toList),
    protectors = Some(protectorSections.toList),
    otherIndividuals = Some(otherIndividualSections.toList),
    trustDetails = Some(trustDetailsSection.toList),
    assets = Some(assetSections.toList)
  )

  when(mockCreateDraftRegistrationService.getAnswerSections(any())(any(), any()))
    .thenReturn(Future.successful(registrationSections))

  "SummaryAnswersController" must {

    val userAnswers = TestUserAnswers.emptyUserAnswers

    val expectedSections: Seq[AnswerSection] = Seq(
      trustDetailsSection.head,
      trusteeSections.head,
      trusteeSections(1),
      beneficiarySections.head,
      beneficiarySections(1),
      assetSections.head,
      assetSections(1),
      protectorSections.head,
      protectorSections(1),
      otherIndividualSections.head,
      otherIndividualSections(1)
    )

    "return OK and the correct view for a GET when tasklist completed for Organisation user" in {

      when(registrationsRepository.getClientReference(any(), any())(any())).thenReturn(Future.successful(None))

      val application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Organisation).build()

      val request = FakeRequest(GET, routes.SummaryAnswerPageController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[SummaryAnswerPageView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(expectedSections, isAgent = false, agentClientRef = "")(request, messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET when tasklist completed for Agent user" in {

      when(registrationsRepository.getClientReference(any(), any())(any())).thenReturn(Future.successful(Some("agentClientReference")))

      val application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(GET, routes.SummaryAnswerPageController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[SummaryAnswerPageView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(expectedSections, isAgent = true, agentClientRef = "agentClientReference")(request, messages).toString

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

      val request = FakeRequest(GET, routes.SummaryAnswerPageController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.TaskListController.onPageLoad(fakeDraftId).url

      application.stop()

    }

  }
}
