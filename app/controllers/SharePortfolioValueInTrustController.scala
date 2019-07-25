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
import controllers.filters.IndexActionFilterProvider
import forms.SharePortfolioValueInTrustFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.SharePortfolioValueInTrustPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.SharePortfolioValueInTrustView

import scala.concurrent.{ExecutionContext, Future}

class SharePortfolioValueInTrustController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: SessionRepository,
                                        navigator: Navigator,
                                        identify: IdentifierAction,
                                        getData: DraftIdRetrievalActionProvider,
                                        requireData: DataRequiredAction,
                                        formProvider: SharePortfolioValueInTrustFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: SharePortfolioValueInTrustView,
                                        validateIndex: IndexActionFilterProvider
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(mode: Mode, index : Int, draftId: String) =
    identify andThen getData(draftId) andThen
      requireData andThen
      validateIndex(index, sections.Assets)

  val form = formProvider()

  def onPageLoad(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(mode, index, draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(SharePortfolioValueInTrustPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId, index))
  }

  def onSubmit(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(mode, index, draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId, index))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(SharePortfolioValueInTrustPage(index), value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(SharePortfolioValueInTrustPage(index), mode, draftId)(updatedAnswers))
        }
      )
  }
}
