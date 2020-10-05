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

package controllers.register.asset.business

import controllers.actions.{RequiredAnswer, StandardActionSets}
import forms.assets.CurrentValueFormProvider
import javax.inject.Inject
import models.registration.pages.Status.Completed
import models.{Mode, NormalMode}
import navigation.Navigator
import pages.entitystatus.AssetStatus
import pages.register.asset.business.{BusinessNamePage, BusinessValuePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import sections.Assets
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.asset.buisness.BusinessValueView

import scala.concurrent.{ExecutionContext, Future}

class BusinessValueController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         registrationsRepository: RegistrationsRepository,
                                         navigator: Navigator,
                                         actionSets: StandardActionSets,
                                         formProvider: CurrentValueFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: BusinessValueView
                                       )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[String] = formProvider()

  private def actions(index: Int, draftId: String) =
    actionSets.identifiedUserWithDataAnswerAndIndex(
      draftId, RequiredAnswer(BusinessNamePage(index),
        routes.BusinessNameController.onPageLoad(NormalMode, index, draftId)),index, Assets)

  def onPageLoad(mode: Mode,  index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val businessName = request.userAnswers.get(BusinessNamePage(index)).get

      val preparedForm = request.userAnswers.get(BusinessValuePage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId, index, businessName))
  }

  def onSubmit(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val businessName = request.userAnswers.get(BusinessNamePage(index)).get

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId, index, businessName))),

        value => {

          val answers = request.userAnswers.set(BusinessValuePage(index), value)
            .flatMap(_.set(AssetStatus(index), Completed))

          for {
                updatedAnswers <- Future.fromTry(answers)
                _              <- registrationsRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(BusinessValuePage(index), mode, draftId)(updatedAnswers))
          }
      )
  }
}
