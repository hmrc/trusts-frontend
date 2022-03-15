/*
 * Copyright 2022 HM Revenue & Customs
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
import forms.PostcodeForTheTrustFormProvider
import pages.register.PostcodeForTheTrustPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.CacheRepository
import services.MatchingService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.PostcodeForTheTrustView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PostcodeForTheTrustController @Inject()(
                                               override val messagesApi: MessagesApi,
                                               cacheRepository: CacheRepository,
                                               actions: StandardActionSets,
                                               formProvider: PostcodeForTheTrustFormProvider,
                                               matchingService: MatchingService,
                                               val controllerComponents: MessagesControllerComponents,
                                               view: PostcodeForTheTrustView
                                             )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[String] = formProvider()

  def onPageLoad(): Action[AnyContent] = actions.identifiedUserMatchingAndSuitabilityData() {
    implicit request =>

      val preparedForm = request.userAnswers.get(PostcodeForTheTrustPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm))
  }

  def onSubmit(): Action[AnyContent] = actions.identifiedUserMatchingAndSuitabilityData().async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(PostcodeForTheTrustPage, value))
            _ <- cacheRepository.set(updatedAnswers)
            redirect <- matchingService.matching(updatedAnswers, request.isAgent)
          } yield redirect
        }
      )
  }
}
