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
import forms.AddAssetsFormProvider
import javax.inject.Inject
import models.{Enumerable, Mode}
import navigation.Navigator
import pages.AddAssetsPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.AddAssetViewHelper
import views.html.AddAssetsView

import scala.concurrent.{ExecutionContext, Future}

class AddAssetsController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DraftIdRetrievalActionProvider,
                                       requireData: DataRequiredAction,
                                       formProvider: AddAssetsFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: AddAssetsView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  private def routes(draftId: String) =
    identify andThen getData(draftId) andThen requireData

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = routes(draftId) {
    implicit request =>

      val assets = new AddAssetViewHelper(request.userAnswers).rows

      Ok(view(form, mode, draftId, assets.inProgress, assets.complete))
  }

  def onSubmit(mode: Mode, draftId: String): Action[AnyContent] = routes(draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          val assets = new AddAssetViewHelper(request.userAnswers).rows
           Future.successful(BadRequest(view(formWithErrors, mode, draftId, assets.inProgress, assets.complete)))
        },

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddAssetsPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddAssetsPage, mode, draftId)(updatedAnswers))
        }
      )
  }
}
