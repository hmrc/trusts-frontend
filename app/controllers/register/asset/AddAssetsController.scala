/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.register.asset

import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import forms.{AddAssetsFormProvider, YesNoFormProvider}
import javax.inject.Inject
import models.registration.pages.AddAssets
import models.registration.pages.AddAssets.NoComplete
import models.requests.RegistrationDataRequest
import models.{Enumerable, Mode}
import navigation.Navigator
import pages.register.asset.{AddAnAssetYesNoPage, AddAssetsPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi, MessagesProvider}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import services.AuditService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.AddAssetViewHelper
import views.html.register.asset.{AddAnAssetYesNoView, AddAssetsView, MaxedOutView}

import scala.concurrent.{ExecutionContext, Future}

class AddAssetsController @Inject()(
                                     override val messagesApi: MessagesApi,
                                     registrationsRepository: RegistrationsRepository,
                                     navigator: Navigator,
                                     identify: RegistrationIdentifierAction,
                                     getData: DraftIdRetrievalActionProvider,
                                     requireData: RegistrationDataRequiredAction,
                                     addAnotherFormProvider: AddAssetsFormProvider,
                                     yesNoFormProvider: YesNoFormProvider,
                                     val controllerComponents: MessagesControllerComponents,
                                     addAssetsView: AddAssetsView,
                                     yesNoView: AddAnAssetYesNoView,
                                     maxedOutView: MaxedOutView,
                                     auditService: AuditService
                                   )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val addAnotherForm: Form[AddAssets] = addAnotherFormProvider()
  val yesNoForm: Form[Boolean] = yesNoFormProvider.withPrefix("addAnAssetYesNo")

  private def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    identify andThen getData(draftId) andThen requireData

  private def heading(count: Int)(implicit mp : MessagesProvider): String = {
    count match {
      case 0 => Messages("addAssets.heading")
      case 1 => Messages("addAssets.singular.heading")
      case size => Messages("addAssets.count.heading", size)
    }
  }

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val assets = new AddAssetViewHelper(auditService)(request.userAnswers, mode, draftId).rows

      assets.count match {
        case 0 => Ok(yesNoView(addAnotherForm, mode, draftId))
        case c if c >= 51 => Ok(maxedOutView(mode, draftId, assets.inProgress, assets.complete, heading(c)))
        case c => Ok(addAssetsView(addAnotherForm, mode, draftId, assets.inProgress, assets.complete, heading(c)))
      }
  }

  def submitOne(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      yesNoForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          Future.successful(BadRequest(yesNoView(formWithErrors, mode, draftId)))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddAnAssetYesNoPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddAnAssetYesNoPage, mode, draftId)(updatedAnswers))
        }
      )
  }

  def submitAnother(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      addAnotherForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          val assets = new AddAssetViewHelper(auditService)(request.userAnswers, mode, draftId).rows

          val count = assets.count

           Future.successful(BadRequest(addAssetsView(formWithErrors, mode, draftId, assets.inProgress, assets.complete, heading(count))))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddAssetsPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddAssetsPage, mode, draftId)(updatedAnswers))
        }
      )
  }

  def submitComplete(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      for {
        updatedAnswers <- Future.fromTry(request.userAnswers.set(AddAssetsPage, NoComplete))
        _ <- registrationsRepository.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(AddAssetsPage, mode, draftId)(updatedAnswers))
  }
}
