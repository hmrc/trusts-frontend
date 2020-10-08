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

import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

import base.RegistrationSpecBase
import models.RegistrationSubmission.AllStatus
import models.registration.pages.AddAssets.NoComplete
import models.registration.pages.Status.Completed
import models.registration.pages._
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.entitystatus._
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.shares._
import pages.register.asset.{AddAssetsPage, WhatKindOfAssetPage}
import pages.register.{RegistrationSubmissionDatePage, RegistrationTRNPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.countryOptions.CountryOptions
import utils.{CheckYourAnswersHelper, TestUserAnswers}
import viewmodels.{AnswerSection, RegistrationAnswerSections}
import views.html.register.ConfirmationAnswerPageView

import scala.concurrent.Future

class ConfirmationAnswersControllerSpec extends RegistrationSpecBase {

  val index = 0

  "ConfirmationAnswersController" must {

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

    val registrationSections = RegistrationAnswerSections(
      beneficiaries = Some(beneficiarySections),
      trustees = Some(trusteeSections),
      trustDetails = Some(trustDetailsSection)
    )

    when(registrationsRepository.getAllStatus(any())(any())).thenReturn(Future.successful(AllStatus.withAllComplete))
    when(mockCreateDraftRegistrationService.getAnswerSections(any())(any())).thenReturn(Future.successful(registrationSections))

    "return OK and the correct view for a GET when tasklist completed" in {

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

          .set(DeceasedSettlorStatus, Status.Completed).success.value

          .set(RegistrationTRNPage, "XNTRN000000001").success.value
          .set(RegistrationSubmissionDatePage, LocalDateTime.now).success.value


      val countryOptions = injector.instanceOf[CountryOptions]
      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId, canEdit = false)

      val expectedSections = Seq(
        trustDetailsSection.head,
        trusteeSections.head,
        trusteeSections(1),
        beneficiarySections.head,
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
        )
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
        view(expectedSections, "XNTRN000000001", trnDateTime)(request, messages).toString

      application.stop()
    }

    "redirect to tasklist page when tasklist not completed" in {

      val userAnswers =
        TestUserAnswers.emptyUserAnswers

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, routes.ConfirmationAnswerPageController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.TaskListController.onPageLoad(fakeDraftId).url

      application.stop()

    }

    "return OK and the correct view for a GET when tasklist completed with living settlor" in {

      val userAnswers =
        TestUserAnswers.emptyUserAnswers
          .set(LivingSettlorStatus(index), Status.Completed).success.value

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

          .set(RegistrationTRNPage, "XNTRN000000001").success.value
          .set(RegistrationSubmissionDatePage, LocalDateTime.now).success.value


      val countryOptions = injector.instanceOf[CountryOptions]
      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId, canEdit = false)

      val expectedSections = Seq(
       trustDetailsSection.head,
        trusteeSections.head,
        trusteeSections(1),
        beneficiarySections.head,
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
        )
      )

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, routes.ConfirmationAnswerPageController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ConfirmationAnswerPageView]

      status(result) mustEqual OK

      val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
      val trnDateTime = LocalDateTime.now.format(dateFormatter)

      contentAsString(result) mustEqual
        view(expectedSections, "XNTRN000000001", trnDateTime)(request, messages).toString

      application.stop()
    }

  }
}
