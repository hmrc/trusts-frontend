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

package views.behaviours

import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.ViewSpecBase

trait ViewBehaviours extends ViewSpecBase {

  def normalPage(view: HtmlFormat.Appendable,
                 sectionKey: Option[String],
                 messageKeyPrefix: String,
                 expectedGuidanceKeys: String*): Unit = {

    "behave like a normal page" when {

      "rendered" must {

        "have the correct banner title" in {

          val doc = asDocument(view)
          val nav = doc.getElementById("proposition-menu")
          val span = nav.children.first
          span.text mustBe messages("site.service_name")
        }

        "display the correct browser title" in {

          val doc = asDocument(view)
          assertEqualsMessage(doc, "title", sectionKey, s"$messageKeyPrefix.title")
        }

        "display the correct page title" in {

          val doc = asDocument(view)
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading")
        }

        "display the correct guidance" in {

          val doc = asDocument(view)
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
        }

        "display language toggles" in {

          val doc = asDocument(view)
          assertRenderedById(doc, "cymraeg-switch")
        }

      }
    }
  }

  def dynamicTitlePage(view: HtmlFormat.Appendable,
                       sectionKey: Option[String],
                       messageKeyPrefix: String,
                       messageKeyParam: String,
                       expectedGuidanceKeys: String*): Unit = {

    "behave like a dynamic title page" when {

      "rendered" must {

        "have the correct banner title" in {

          val doc = asDocument(view)
          val nav = doc.getElementById("proposition-menu")
          val span = nav.children.first
          span.text mustBe messages("site.service_name")
        }

        "display the correct browser title" in {

          val doc = asDocument(view)
          assertEqualsMessage(doc, "title", sectionKey, s"$messageKeyPrefix.title", messageKeyParam)
        }

        "display the correct page title" in {

          val doc = asDocument(view)
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading", messageKeyParam)
        }

        "display the correct guidance" in {

          val doc = asDocument(view)
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
        }

        "display language toggles" in {

          val doc = asDocument(view)
          assertRenderedById(doc, "cymraeg-switch")
        }

      }
    }
  }

  def dynamicTitlePage(view: HtmlFormat.Appendable,
                       sectionKey: Option[String],
                       messageKeyPrefix: String,
                       args: Seq[String],
                       expectedGuidanceKeys: String*): Unit = {

    "behave like a dynamic title page" when {

      "rendered" must {

        "have the correct banner title" in {

          val doc = asDocument(view)
          val nav = doc.getElementById("proposition-menu")
          val span = nav.children.first
          span.text mustBe messages("site.service_name")
        }

        "display the correct browser title" in {

          val doc = asDocument(view)
          assertEqualsMessage(doc, "title", sectionKey, s"$messageKeyPrefix.title", args: _*)
        }

        "display the correct page title" in {

          val doc = asDocument(view)
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading", args: _*)
        }

        "display the correct guidance" in {

          val doc = asDocument(view)
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
        }

        "display language toggles" in {

          val doc = asDocument(view)
          assertRenderedById(doc, "cymraeg-switch")
        }

      }
    }
  }

  def confirmationPage(view: HtmlFormat.Appendable,
                       messageKeyPrefix: String,
                       refNumber: String,
                       leadTrusteeName: String,
                       sectionKey: Option[String],
                       expectedGuidanceKeys: String*): Unit = {

    "behave like a confirmation page" when {

      "rendered" must {

        "have the correct banner title" in {

          val doc = asDocument(view)
          val nav = doc.getElementById("proposition-menu")
          val span = nav.children.first
          span.text mustBe messages("site.service_name")
        }

        "display the correct browser title" in {

          val doc = asDocument(view)
          assertEqualsMessage(doc, "title", sectionKey, "confirmation.title")
        }

        "display the correct page title" in {

          val doc = asDocument(view)
          assertContainsText(doc, messages("confirmation.heading1"))
          assertContainsText(doc, messages("confirmation.heading2"))
          assertContainsText(doc, refNumber)
        }

        "display the correct guidance" in {

          val doc = asDocument(view)
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
          assertContainsText(doc, messages(s"$messageKeyPrefix.p1", leadTrusteeName))
        }

        "display language toggles" in {

          val doc = asDocument(view)
          assertRenderedById(doc, "cymraeg-switch")
        }

      }
    }
  }

  def pageWithBackLink(view: HtmlFormat.Appendable): Unit = {

    "behave like a page with a back link" must {

      "have a back link" in {

        val doc = asDocument(view)
        assertRenderedById(doc, "back-link")
      }
    }
  }

  def pageWithLink(view: HtmlFormat.Appendable, id: String, url : String): Unit = {

    "behave like a page with a link" must {

      "have a link" in {

        val doc = asDocument(view)
        val element = doc.getElementById(id)

        assertRenderedById(doc, id)
        assertAttributeValueForElement(element, "href", url)
      }
    }
  }

  def pageWithASubmitButton(view: HtmlFormat.Appendable): Unit = {

    "behave like a page with a submit button" must {
      "have a submit button" in {
        val doc = asDocument(view)
        assertRenderedById(doc, "submit")
      }
    }
  }

  def pageWithContinueButton(view: HtmlFormat.Appendable, url : String): Unit = {

    "behave like a page with a Continue button" must {
      "have a continue button" in {
        val doc = asDocument(view)
        assertContainsTextForId(doc,"button", "Continue")
        assertAttributeValueForElement(
          doc.getElementById("button"),
          "href",
          url
        )
      }
    }
  }

  def pageWithASignOutButton(view: HtmlFormat.Appendable): Unit = {

    "behave like a page with a sign-out button" must {
      "have a sign-out button" in {
        val doc = asDocument(view)
        assertRenderedById(doc, "sign-out")
      }
    }
  }

  def pageWithSubHeading(view: HtmlFormat.Appendable, text: String): Unit = {

    "behave like a page with a sub-heading" must {

      "have a sub-heading" in {

        val doc = asDocument(view)
        assertContainsText(doc, text)
      }
    }
  }

  def pageWithHint[A](form: Form[A],
                      createView: Form[A] => HtmlFormat.Appendable,
                      expectedHintKey: String): Unit = {

    "behave like a page with hint text" in {

      val doc = asDocument(createView(form))
      assertContainsHint(doc, "value", Some(messages(expectedHintKey)))
    }
  }
}


