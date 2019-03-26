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

import controllers.actions._
import forms.WhatKindOfAssetFormProvider
import javax.inject.Inject
import models.WhatKindOfAsset.Money
import models.{Enumerable, Mode}
import navigation.Navigator
import pages.WhatKindOfAssetPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.WhatKindOfAssetView

import scala.concurrent.{ExecutionContext, Future}

class WhatKindOfAssetController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       validateIndex: IndexActionFilterProvider,
                                       formProvider: WhatKindOfAssetFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: WhatKindOfAssetView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  def routes(index : Int) =
    identify andThen getData andThen requireData andThen validateIndex(index, pages.Assets)

  def onPageLoad(mode: Mode, index: Int): Action[AnyContent] = routes(index) {
    implicit request =>
      val preparedForm = request.userAnswers.get(WhatKindOfAssetPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, index))
  }

  def onSubmit(mode: Mode, index: Int): Action[AnyContent] = routes(index).async {
    implicit request =>

      val assets = request.userAnswers.get(pages.Assets).getOrElse(Nil)
      if(assets.exists(_.whatKindOfAsset.contains(Money))) {
        Future.successful(BadRequest(view(form, mode, index)))
      } else {
        form.bindFromRequest().fold(
          (formWithErrors: Form[_]) =>
            Future.successful(BadRequest(view(formWithErrors, mode, index))),

          value => {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(WhatKindOfAssetPage(index), value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(WhatKindOfAssetPage(index), mode)(updatedAnswers))
          }
        )
      }
  }
}
