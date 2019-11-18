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

import config.FrontendAppConfig
import controllers.actions._
import forms.YesNoFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.TrustHaveAUTRPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import viewmodels.Link
import views.html.TrustHaveAUTRView

import scala.concurrent.{ExecutionContext, Future}

class TrustHaveAUTRController @Inject()(override val messagesApi: MessagesApi,
                                        registrationsRepository: RegistrationsRepository,
                                        navigator: Navigator,
                                        identify: IdentifyForRegistration,
                                        getData: DraftIdRetrievalActionProvider,
                                        requireData: DataRequiredAction,
                                        formProvider: YesNoFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: TrustHaveAUTRView,
                                        config : FrontendAppConfig
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String) = identify andThen getData(draftId) andThen requireData

  private val lostUtrKey : String = "trustHaveAUTR.link"

  val form: Form[Boolean] = formProvider.withPrefix("trustHaveAUTR")

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(TrustHaveAUTRPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val link = Link(Messages(lostUtrKey), config.lostUtrUrl)

      Ok(view(preparedForm, mode, draftId, Some(link)))
  }

  def onSubmit(mode: Mode, draftId: String) = actions(draftId).async {
    implicit request =>

      val link = Link(Messages(lostUtrKey), config.lostUtrUrl)

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId, Some(link)))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TrustHaveAUTRPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(TrustHaveAUTRPage, mode, draftId, request.affinityGroup)(updatedAnswers))
        }
      )
  }
}
