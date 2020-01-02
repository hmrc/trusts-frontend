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

package controllers.register.settlors.living_settlor

import controllers.actions._
import controllers.filters.IndexActionFilterProvider
import forms.living_settlor.SettlorBusinessNameFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.register.settlors.living_settlor.SettlorBusinessNamePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import sections.LivingSettlors
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.annotations.LivingSettlor
import views.html.register.settlors.living_settlor.SettlorBusinessNameView

import scala.concurrent.{ExecutionContext, Future}

class SettlorBusinessNameController @Inject()(
                                               override val messagesApi: MessagesApi,
                                               registrationsRepository: RegistrationsRepository,
                                               @LivingSettlor navigator: Navigator,
                                               identify: IdentifierAction,
                                               getData: DraftIdRetrievalActionProvider,
                                               validateIndex : IndexActionFilterProvider,
                                               requireData: DataRequiredAction,
                                               formProvider: SettlorBusinessNameFormProvider,
                                               val controllerComponents: MessagesControllerComponents,
                                               view: SettlorBusinessNameView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode, index: Int, draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData andThen validateIndex(index, LivingSettlors)) {
    implicit request =>

      val preparedForm = request.userAnswers.get(SettlorBusinessNamePage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, index, draftId))
  }

  def onSubmit(mode: Mode, index: Int, draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData andThen validateIndex(index, LivingSettlors)).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, index, draftId))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(SettlorBusinessNamePage(index),  value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(SettlorBusinessNamePage(index), mode, draftId)(updatedAnswers))
        }
      )
  }
}
