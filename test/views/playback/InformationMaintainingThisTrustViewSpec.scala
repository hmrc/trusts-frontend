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

package views.playback

import config.FrontendAppConfig
import controllers.playback.routes
import views.behaviours.ViewBehaviours
import views.html.playback.InformationMaintainingThisTrustView

class InformationMaintainingThisTrustViewSpec extends ViewBehaviours {

  "InformationMaintainingThisTrust view" when {

    "declaration enabled" must {

      val utr = "1234545678"

      val application = applicationBuilder(Some(emptyUserAnswers))
        .configure(Seq(
          "microservice.services.features.declare.enabled" -> true
        ): _*)
        .build()

      val view = application.injector.instanceOf[InformationMaintainingThisTrustView]

      application.stop()

      val applyView = view.apply(utr)(fakeRequest, messages)

      "Have a dynamic utr in the subheading" in {
        val doc = asDocument(applyView)
        assertContainsText(doc, s"This trust’s UTR: $utr")
      }

      behave like normalPage(applyView, "informationMaintainingThisTrust",
        "warning",
        "h3",
        "paragraph1",
        "list1",
        "list2",
        "printsave.link",
        "list3",
        "list4",
        "h2",
        "paragraph2",
        "paragraph3"
      )

      behave like pageWithBackLink(applyView)

      behave like pageWithContinueButton(applyView, routes.DeclarationWhatNextController.onPageLoad().url)
    }

    "declaration disabled" must {

      val utr = "1234545678"

      val application = applicationBuilder(Some(emptyUserAnswers))
        .configure(Seq(
          "microservice.services.features.declare.enabled" -> false
        ): _*)
        .build()

      val view = application.injector.instanceOf[InformationMaintainingThisTrustView]

      application.stop()

      val applyView = view.apply(utr)(fakeRequest, messages)

      "Have a dynamic utr in the subheading" in {
        val doc = asDocument(applyView)
        assertContainsText(doc, s"This trust’s UTR: $utr")
      }

      "not have a continue button" in {
        val doc = asDocument(applyView)
        assertNotRenderedById(doc, "button")
      }

      behave like normalPage(applyView, "informationMaintainingThisTrust",
        "warning",
        "h3",
        "paragraph1",
        "list1",
        "list2",
        "printsave.link",
        "list3",
        "list4",
        "h2",
        "paragraph2",
        "paragraph3"
      )

      behave like pageWithBackLink(applyView)
    }

  }
}
