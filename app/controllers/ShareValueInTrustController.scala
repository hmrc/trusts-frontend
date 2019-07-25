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
import forms.ShareValueInTrustFormProvider
import javax.inject.Inject
import models.{Mode, NormalMode}
import navigation.Navigator
import pages.{ShareCompanyNamePage, ShareValueInTrustPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.ShareValueInTrustView

import scala.concurrent.{ExecutionContext, Future}

class ShareValueInTrustController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: SessionRepository,
                                        navigator: Navigator,
                                        identify: IdentifierAction,
                                        getData: DraftIdRetrievalActionProvider,
                                        requireData: DataRequiredAction,
                                        formProvider: ShareValueInTrustFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: ShareValueInTrustView,
                                        requiredAnswer: RequiredAnswerActionProvider,
                                        validateIndex: IndexActionFilterProvider
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  private def actions(mode: Mode, index : Int, draftId: String) =
    identify andThen getData(draftId) andThen
      requireData andThen
      validateIndex(index, sections.Assets) andThen
      requiredAnswer(RequiredAnswer(
        ShareCompanyNamePage(index),
        routes.ShareCompanyNameController.onPageLoad(NormalMode, index, draftId))
      )

  def onPageLoad(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(mode, index, draftId) {
    implicit request =>

      val companyName = request.userAnswers.get(ShareCompanyNamePage(index)).get.toString

      val preparedForm = request.userAnswers.get(ShareValueInTrustPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId, index, companyName))
  }

  def onSubmit(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(mode, index, draftId).async {
    implicit request =>

      val companyName = request.userAnswers.get(ShareCompanyNamePage(index)).get.toString

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId, index, companyName))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ShareValueInTrustPage(index), value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(ShareValueInTrustPage(index), mode, draftId)(updatedAnswers))
        }
      )
  }
}
