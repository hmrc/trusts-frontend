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

package config

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.{Lang, Messages}
import play.api.mvc.{Call, Request}

import java.net.{URI, URLEncoder}

@Singleton
class FrontendAppConfig @Inject() (val configuration: Configuration) {

  final val ENGLISH = "en"
  final val WELSH = "cy"
  final val UK_COUNTRY_CODE = "GB"

  private val contactHost = configuration.get[String]("contact-frontend.host")
  private val contactFormServiceIdentifier = "trusts"

  private def loadConfig(key: String): String = configuration.get[String](key)

  val analyticsToken: String = configuration.get[String](s"google-analytics.token")

  val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"

  val betaFeedbackUrl = s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier"
  val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated?service=$contactFormServiceIdentifier"

  val whoShouldRegisterUrl: String = configuration.get[String]("urls.whoShouldRegister")
  val trustsAndTaxesUrl: String = configuration.get[String]("urls.trustsAndTaxes")
  val ggSignInUrl: String = configuration.get[String]("urls.ggSignIn")

  lazy val loginUrl: String = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")
  lazy val lostUtrUrl : String = configuration.get[String]("urls.lostUtr")
  lazy val logoutUrl: String = loadConfig("urls.logout")

  def beneficiariesFrontendUrl(draftId: String): String = frontendUrl(draftId, "beneficiaries")

  def taxLiabilityFrontendUrl(draftId: String): String = frontendUrl(draftId, "taxLiability")

  def trusteesFrontendUrl(draftId: String): String = frontendUrl(draftId, "trustees")

  def trustDetailsFrontendUrl(draftId: String): String = frontendUrl(draftId, "trustDetails")

  def settlorsFrontendUrl(draftId: String): String = frontendUrl(draftId, "settlors")

  def protectorsFrontendUrl(draftId: String): String = frontendUrl(draftId, "protectors")

  def otherIndividualsFrontendUrl(draftId: String): String = frontendUrl(draftId, "otherIndividuals")

  def assetsFrontendUrl(draftId: String): String = frontendUrl(draftId, "assets")

  def agentDetailsFrontendUrl(draftId: String): String = frontendUrl(draftId, "agentDetails")

  private def frontendUrl(draftId: String, section: String): String = {
    lazy val url: String = loadConfig(s"urls.${section}Frontend")
    url.replace(":draftId", draftId)
  }

  lazy val agentServiceRegistrationUrl: String = {
    lazy val agentsSubscriptionsUrl : String = configuration.get[String]("urls.agentSubscriptions")
    s"$agentsSubscriptionsUrl?continue=$loginContinueUrl"
  }

  lazy val locationCanonicalList: String = configuration.get[String]("location.canonical.list.all")
  lazy val locationCanonicalListCY: String = configuration.get[String]("location.canonical.list.allCY")

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  lazy val logoutAudit: Boolean =
    configuration.get[Boolean]("microservice.services.features.auditing.logout")

  lazy val ttlInSeconds: Int = configuration.get[Int]("mongodb.registration.ttlSeconds")

  lazy val trustsUrl: String = configuration.get[Service]("microservice.services.trusts").baseUrl

  lazy val authUrl: String = configuration.get[Service]("microservice.services.auth").baseUrl

  lazy val trustsStoreUrl: String = configuration.get[Service]("microservice.services.trusts-store").baseUrl

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang(ENGLISH),
    "cymraeg" -> Lang(WELSH)
  )

  def routeToSwitchLanguage: String => Call =
    (lang: String) => controllers.register.routes.LanguageSwitchController.switchToLanguage(lang)

  lazy val auditSubmissions : Boolean =
    configuration.get[Boolean]("microservice.services.features.auditing.submissions.enabled")

  lazy val declarationEmailEnabled: Boolean = configuration.get[Boolean]("microservice.services.features.declaration.email.enabled")

  lazy val maintainATrustFrontendUrl : String =
    configuration.get[String]("urls.maintainATrust")
  lazy val createAgentServicesAccountUrl : String =
    configuration.get[String]("urls.createAgentServicesAccount")

  lazy val countdownLength: String = configuration.get[String]("timeout.countdown")
  lazy val timeoutLength: String = configuration.get[String]("timeout.length")

  def accessibilityLinkUrl(implicit request: Request[_]): String = {
    lazy val accessibilityBaseLinkUrl: String = configuration.get[String]("urls.accessibility")
    val userAction = URLEncoder.encode(new URI(request.uri).getPath, "UTF-8")
    s"$accessibilityBaseLinkUrl?userAction=$userAction"
  }

  def helplineUrl(implicit messages: Messages): String = {
    val path = messages.lang.code match {
      case WELSH => "urls.welshHelpline"
      case _ => "urls.trustsHelpline"
    }
    configuration.get[String](path)
  }

}
