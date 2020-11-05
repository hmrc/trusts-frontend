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

package config

import java.net.{URI, URLEncoder}
import java.time.LocalDate

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.{Call, Request}

@Singleton
class FrontendAppConfig @Inject() (val configuration: Configuration) {

  private val contactHost = configuration.get[String]("contact-frontend.host")
  private val contactFormServiceIdentifier = "trusts"

  lazy val serviceName: String = configuration.get[String]("serviceName")

  private def loadConfig(key: String) = configuration.get[String](key)

  val analyticsToken: String = configuration.get[String](s"google-analytics.token")
  val analyticsHost: String = configuration.get[String](s"google-analytics.host")

  val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"

  val betaFeedbackUrl = s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier"
  val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated?service=$contactFormServiceIdentifier"

  val whoShouldRegisterUrl: String = configuration.get[String]("urls.whoShouldRegister")
  val trustsAndTaxesUrl: String = configuration.get[String]("urls.trustsAndTaxes")
  val trustsHelplineUrl: String = configuration.get[String]("urls.trustsHelpline")
  val ggSignInUrl: String = configuration.get[String]("urls.ggSignIn")

  lazy val loginUrl: String = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")
  lazy val lostUtrUrl : String = configuration.get[String]("urls.lostUtr")
  lazy val logoutUrl: String = loadConfig("urls.logout")

  private def insertDraftId(url: String, draftId: String) = url.replace(":draftId", draftId)

  private lazy val beneficiariesFrontendUrlTemplate: String = loadConfig("urls.beneficiariesFrontend")
  def beneficiariesFrontendUrl(draftId: String): String = insertDraftId(beneficiariesFrontendUrlTemplate, draftId)

  private lazy val taxLiabilityFrontendUrlTemplate: String = loadConfig("urls.taxLiabilityFrontend")
  def taxLiabilityFrontendUrl(draftId: String): String = insertDraftId(taxLiabilityFrontendUrlTemplate, draftId)

  private lazy val trusteesFrontendUrlTemplate: String = loadConfig("urls.trusteesFrontend")
  def trusteesFrontendUrl(draftId: String): String = insertDraftId(trusteesFrontendUrlTemplate, draftId)

  private lazy val trustDetailsFrontendUrlTemplate: String = loadConfig("urls.trustDetailsFrontend")
  def trustDetailsFrontendUrl(draftId: String): String = insertDraftId(trustDetailsFrontendUrlTemplate, draftId)

  private lazy val settlorsFrontendUrlTemplate: String = loadConfig("urls.settlorsFrontend")
  def settlorsFrontendUrl(draftId: String): String = insertDraftId(settlorsFrontendUrlTemplate, draftId)

  private lazy val protectorsFrontendUrlTemplate: String = loadConfig("urls.protectorsFrontend")
  def protectorsFrontendUrl(draftId: String): String = insertDraftId(protectorsFrontendUrlTemplate, draftId)

  private lazy val otherIndividualsFrontendUrlTemplate: String = loadConfig("urls.otherIndividualsFrontend")
  def otherIndividualsFrontendUrl(draftId: String): String = insertDraftId(otherIndividualsFrontendUrlTemplate, draftId)

  lazy val otacUrl : String = configuration.get[String]("urls.otacLogin")

  lazy val agentsSubscriptionsUrl : String = configuration.get[String]("urls.agentSubscriptions")
  lazy val agentServiceRegistrationUrl = s"$agentsSubscriptionsUrl?continue=$loginContinueUrl"

  lazy val locationCanonicalList: String = loadConfig("location.canonical.list.all")
  lazy val locationCanonicalListNonUK: String = loadConfig("location.canonical.list.nonUK")

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  lazy val ttlInSeconds = configuration.get[Int]("mongodb.registration.ttlSeconds")

  lazy val trustsUrl = configuration.get[Service]("microservice.services.trusts").baseUrl

  lazy val authUrl = configuration.get[Service]("microservice.services.auth").baseUrl

  lazy val trustsStoreUrl: String = configuration.get[Service]("microservice.services.trusts-store").baseUrl + "/trusts-store"

  lazy val  posthmrc: String = configuration.get[String]("confirmation.posthmrc")

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )

  def routeToSwitchLanguage: String => Call =
    (lang: String) => controllers.register.routes.LanguageSwitchController.switchToLanguage(lang)

  lazy val removeTaxLiabilityOnTaskList : Boolean =
    configuration.get[Boolean]("microservice.services.features.removeTaxLiabilityOnTaskList")

  lazy val enableWhitelist : Boolean = configuration.get[String]("microservice.services.features.whitelist.enabled").toBoolean

  lazy val campaignWhitelistEnabled : Boolean = configuration.get[Boolean]("microservice.services.features.campaignWhitelist.enabled")
  lazy val auditSubmissions : Boolean =
    configuration.get[Boolean]("microservice.services.features.auditing.submissions.enabled")

  lazy val auditCannotCreateRegistration : Boolean =
    configuration.get[Boolean]("microservice.services.features.auditing.cannotCreateRegistration.enabled")

  lazy val livingSettlorBusinessEnabled : Boolean = configuration.get[Boolean]("microservice.services.features.journey.livingSettlorBusiness.enabled")

  lazy val declarationEmailEnabled: Boolean = configuration.get[Boolean]("microservice.services.features.declaration.email.enabled")

  lazy val maintainATrustFrontendUrl : String =
    configuration.get[String]("urls.maintainATrust")

  lazy val countdownLength: String = configuration.get[String]("timeout.countdown")
  lazy val timeoutLength: String = configuration.get[String]("timeout.length")

  private val day: Int = configuration.get[Int]("minimumDate.day")
  private val month: Int = configuration.get[Int]("minimumDate.month")
  private val year: Int = configuration.get[Int]("minimumDate.year")
  lazy val minDate: LocalDate = LocalDate.of(year, month, day)

  lazy val maximumValue: Long = configuration.get[Long]("maximumValue")

  private lazy val accessibilityBaseLinkUrl: String = configuration.get[String]("urls.accessibility")

  def accessibilityLinkUrl(implicit request: Request[_]): String = {
    val userAction = URLEncoder.encode(new URI(request.uri).getPath, "UTF-8")
    s"$accessibilityBaseLinkUrl?userAction=$userAction"
  }

}
