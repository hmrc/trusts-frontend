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

package controllers.register

import controllers.actions.StandardActionSets
import models.requests.RegistrationDataRequest
import pages.register.{MatchingNamePage, PostcodeForTheTrustPage, TrustRegisteredWithUkAddressYesNoPage, WhatIsTheUTRPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.TrustAlreadyRegisteredView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TrustAlreadyRegisteredController @Inject()(
                                                  override val messagesApi: MessagesApi,
                                                  registrationsRepository: RegistrationsRepository,
                                                  standardActionSets: StandardActionSets,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  view: TrustAlreadyRegisteredView
                                                )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    standardActionSets.identifiedUserWithRegistrationData(draftId)

  private def redirect(): Result = Redirect(routes.WhatIsTheUTRController.onPageLoad())

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      request.userAnswers.get(WhatIsTheUTRPage) match {
        case Some(utr) => Ok(view(draftId, utr, request.isAgent))
        case _ => redirect()
      }
  }

  def onSubmit(draftId : String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      for {
        updatedAnswers <- Future.fromTry(request.userAnswers
          .remove(WhatIsTheUTRPage)
          .flatMap(_.remove(MatchingNamePage))
          .flatMap(_.remove(TrustRegisteredWithUkAddressYesNoPage))
          .flatMap(_.remove(PostcodeForTheTrustPage))
        )
        _ <- registrationsRepository.set(updatedAnswers)
      } yield redirect()
  }
}
