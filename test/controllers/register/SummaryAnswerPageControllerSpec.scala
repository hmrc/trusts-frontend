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

package controllers.register

import base.RegistrationSpecBase
import models.RegistrationSubmission.AllStatus
import models.registration.pages.AddAssets.NoComplete
import models.registration.pages.Status.Completed
import models.registration.pages._
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.entitystatus._
import pages.register.agents.AgentInternalReferencePage
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.shares._
import pages.register.asset.{AddAssetsPage, WhatKindOfAssetPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import utils.countryOptions.CountryOptions
import utils.{CheckYourAnswersHelper, DateFormatterImpl, TestUserAnswers}
import viewmodels.{AnswerSection, RegistrationAnswerSections}
import views.html.register.SummaryAnswerPageView

import scala.concurrent.Future

class SummaryAnswerPageControllerSpec extends RegistrationSpecBase {

  private val index = 0

  val trustDetailsSection = List(
    AnswerSection(
      Some("trustDetailsHeadingKey1"),
      List.empty,
      Some("trustDetailsSectionKey1")
    )
  )

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

  val protectorSections = List(
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

  val otherIndividualSections = List(
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

  val registrationSections = RegistrationAnswerSections(
    beneficiaries = Some(beneficiarySections),
    trustees = Some(trusteeSections),
    protectors = Some(protectorSections),
    otherIndividuals = Some(otherIndividualSections),
    trustDetails = Some(trustDetailsSection)
  )

  when(mockCreateDraftRegistrationService.getAnswerSections(any())(any())).thenReturn(Future.successful(registrationSections))

  "SummaryAnswersController" must {

    val userAnswers =
      TestUserAnswers.emptyUserAnswers

        .set(WhatKindOfAssetPage(index), WhatKindOfAsset.Money).success.value
        .set(AssetMoneyValuePage(index), "100").success.value
        .set(AssetStatus(index), Completed).success.value
        .set(WhatKindOfAssetPage(1), WhatKindOfAsset.Shares).success.value
        .set(SharesInAPortfolioPage(1), true).success.value
        .set(SharePortfolioNamePage(1), "Company").success.value
        .set(SharePortfolioOnStockExchangePage(1), true).success.value
        .set(SharePortfolioQuantityInTrustPage(1), "1234").success.value
        .set(SharePortfolioValueInTrustPage(1), "4000").success.value
        .set(AssetStatus(1), Completed).success.value
        .set(AddAssetsPage, NoComplete).success.value

        .set(AgentInternalReferencePage, "agentClientReference").success.value

    val countryOptions = injector.instanceOf[CountryOptions]
    val dateFormatterImpl: DateFormatterImpl = injector.instanceOf[DateFormatterImpl]
    val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, dateFormatterImpl)(userAnswers,fakeDraftId, canEdit = false)

    val expectedSections = Seq(
      trustDetailsSection(0),
      trusteeSections(0),
      trusteeSections(1),
      beneficiarySections(0),
      beneficiarySections(1),
      AnswerSection(None, Nil, Some("Assets")),
      AnswerSection(
        Some("Money"),
        Seq(
          checkYourAnswersHelper.assetMoneyValue(index).value
        ),
        None
      ),
      AnswerSection(
        Some("Share 1"),
        Seq(
          checkYourAnswersHelper.sharesInAPortfolio(1).value,
          checkYourAnswersHelper.sharePortfolioName(1).value,
          checkYourAnswersHelper.sharePortfolioOnStockExchange(1).value,
          checkYourAnswersHelper.sharePortfolioQuantityInTrust(1).value,
          checkYourAnswersHelper.sharePortfolioValueInTrust(1).value
        ),
        None
      ),
      protectorSections(0),
      protectorSections(1),
      otherIndividualSections(0),
      otherIndividualSections(1)
    )

    when(registrationsRepository.getAllStatus(any())(any())).thenReturn(Future.successful(AllStatus.withAllComplete))

    "return OK and the correct view for a GET when tasklist completed for Organisation user" in {

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

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, routes.SummaryAnswerPageController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.TaskListController.onPageLoad(fakeDraftId).url

      application.stop()

    }

  }

}
