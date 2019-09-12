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

package controllers.living_settlor

import controllers.actions._
import controllers.filters.IndexActionFilterProvider
import forms.living_settlor.SettlorBusinessDetailsFormProvider
import javax.inject.Inject
import models.{Enumerable, Mode}
import navigation.Navigator
import pages.living_settlor.{SettlorBusinessDetailsPage, SettlorBusinessNamePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import sections.LivingSettlors
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.annotations.LivingSettlor
import views.html.living_settlor.SettlorBusinessDetailsView

import scala.concurrent.{ExecutionContext, Future}

class SettlorBusinessDetailsController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       @LivingSettlor navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DraftIdRetrievalActionProvider,
                                       validateIndex : IndexActionFilterProvider,
                                       requireData: DataRequiredAction,
                                       formProvider: SettlorBusinessDetailsFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: SettlorBusinessDetailsView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad(mode: Mode, index: Int, draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData andThen validateIndex(index, LivingSettlors)) {
    implicit request =>


      val preparedForm = request.userAnswers.get(SettlorBusinessDetailsPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      request.userAnswers.get(SettlorBusinessNamePage(index)).map { name =>
        Ok(view(preparedForm, mode, index, draftId, name))
      } getOrElse Redirect(controllers.routes.SessionExpiredController.onPageLoad())
  }

  def onSubmit(mode: Mode, index : Int, draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData andThen validateIndex(index, LivingSettlors)).async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful {
            request.userAnswers.get(SettlorBusinessNamePage(index)).map { name =>
              BadRequest(view(formWithErrors, mode, index, draftId, name))
            } getOrElse Redirect(controllers.routes.SessionExpiredController.onPageLoad())
          },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(SettlorBusinessDetailsPage(index), value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(SettlorBusinessDetailsPage(index), mode, draftId)(updatedAnswers))
        }
      )
  }
}
