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

package views.behaviours

import org.jsoup.select.Elements
import play.twirl.api.HtmlFormat
import viewmodels.TrusteeRow
import views.ViewSpecBase

trait TabularDataViewBehaviours extends ViewSpecBase {

  def pageWithNoTabularData(view: HtmlFormat.Appendable) = {

    "behave like a page with tabular data" when {

      "rendered with no data" in {
        val doc = asDocument(view)
        assertElementNotPresent(doc, "dl")
      }

    }

  }


  def pageWithTabularData(view: HtmlFormat.Appendable, data : Seq[TrusteeRow]) = {

    "behave like a page with tabular data" should {

      "render an add to list" in {
        val doc = asDocument(view)
        assertRenderedByCssSelector(doc, ".hmrc-add-to-a-list")
      }

      "render a row for each data item" in {
        val doc = asDocument(view)
        val elements : Elements = doc.select(".hmrc-add-to-a-list__contents")

        elements.size mustBe data.size

        val dataWithIndex = data.zipWithIndex

        for ((item, index) <- dataWithIndex) {
          val element = elements.get(index)

          element.text must include(item.name)
          element.text must include(messages(item.typeKey))

          val changeLink = element.getElementsByClass("hmrc-add-to-a-list__change").first()

          changeLink.getElementsByTag("a").attr("href") must include(item.changeUrl)
          changeLink.text must include(s"Change ${item.name}")

          val removeLink = element.getElementsByClass("hmrc-add-to-a-list__remove").first()

          removeLink.getElementsByTag("a").attr("href") must include(item.removeUrl)
          removeLink.text must include(s"Delete ${item.name}")
        }
      }

    }

  }

}
