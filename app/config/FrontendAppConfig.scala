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

import com.google.inject.{Inject, Singleton}
import controllers.routes
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.Call

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

  lazy val loginUrl: String = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")
  lazy val lostUtrUrl : String = configuration.get[String]("urls.lostUtr")

  lazy val otacUrl : String = configuration.get[String]("urls.otacLogin")

  lazy val agentsSubscriptionsUrl : String = configuration.get[String]("urls.agentSubscriptions")
  lazy val agentServiceRegistrationUrl = s"$agentsSubscriptionsUrl?continue=$loginContinueUrl"

  lazy val locationCanonicalList: String = loadConfig("location.canonical.list.all")
  lazy val locationCanonicalListNonUK: String = loadConfig("location.canonical.list.nonUK")

  lazy val relationshipName : String =
    configuration.get[String]("microservice.services.self.relationship-establishment.name")
  lazy val relationshipIdentifier : String =
    configuration.get[String]("microservice.services.self.relationship-establishment.identifier")

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  lazy val ttlInSeconds = configuration.get[Int]("mongodb.registration.ttlSeconds")

  lazy val trustsUrl = configuration.get[Service]("microservice.services.trusts").baseUrl

  lazy val authUrl = configuration.get[Service]("microservice.services.auth").baseUrl

  def claimATrustUrl(utr: String) =
    configuration.get[Service]("microservice.services.claim-a-trust-frontend").baseUrl + s"/claim-a-trust/save/$utr"

  def verifyIdentityForATrustUrl(utr: String) =
    configuration.get[Service]("microservice.services.verify-your-identity-for-a-trust-frontend").baseUrl + s"/verify-your-identity-for-a-trust/save/$utr"

  lazy val enrolmentStoreProxyUrl = configuration.get[Service]("microservice.services.enrolment-store-proxy").baseUrl

  lazy val trustsStoreUrl: String = configuration.get[Service]("microservice.services.trusts-store").baseUrl + "/trusts-store"

  lazy val agentInvitationsUrl: String = configuration.get[String]("urls.agentInvitations")

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

  lazy val claimEnabled: Boolean = configuration.get[Boolean]("microservice.services.features.claim.enabled")

  lazy val playbackEnabled: Boolean = configuration.get[Boolean]("microservice.services.features.playback.enabled")

  lazy val declarationEnabled: Boolean = configuration.get[Boolean]("microservice.services.features.declare.enabled")

  lazy val declarationEmailEnabled: Boolean = configuration.get[Boolean]("microservice.services.features.declaration.email.enabled")

  lazy val useMaintainFrontend : Boolean =
    configuration.get[Boolean]("microservice.services.features.useMaintainFrontend.enabled")

  lazy val maintainATrustFrontendUrl : String =
    configuration.get[String]("urls.maintainATrust")
}
