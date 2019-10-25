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

package config

import com.google.inject.{Inject, Singleton}
import controllers.routes
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.Call

@Singleton
class FrontendAppConfig @Inject() (val configuration: Configuration) {

  private val contactHost = configuration.get[String]("contact-frontend.host")
  private val contactFormServiceIdentifier = "trusts-frontend"

  private def loadConfig(key: String) = configuration.get[String](key)

  val analyticsToken: String = configuration.get[String](s"google-analytics.token")
  val analyticsHost: String = configuration.get[String](s"google-analytics.host")

  val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"

  val betaFeedbackUrl = s"$contactHost/contact/beta-feedback"
  val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated"

  lazy val loginUrl: String = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")
  lazy val lostUtrUrl : String = configuration.get[String]("urls.lostUtr")

  lazy val otacUrl : String = configuration.get[String]("urls.otacLogin")

  lazy val agentsSubscriptionsUrl : String = configuration.get[String]("urls.agentSubscriptions")
  lazy val agentServiceRegistrationUrl = s"$agentsSubscriptionsUrl?continue=$loginContinueUrl"

  lazy val locationCanonicalList: String = loadConfig("location.canonical.list.all")
  lazy val locationCanonicalListNonUK: String = loadConfig("location.canonical.list.nonUK")

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  lazy val ttlInSeconds = configuration.get[Int]("mongodb.timeToLiveInSeconds")

  lazy val trustsUrl = configuration.get[Service]("microservice.services.trusts").baseUrl

  lazy val authUrl = configuration.get[Service]("microservice.services.auth").baseUrl

  def claimATrustUrl(utr: String) = configuration.get[Service]("microservice.services.claim-a-trust-frontend").baseUrl + s"/claim-a-trust/save/$utr"

  lazy val  posthmrc: String = configuration.get[String]("confirmation.posthmrc")

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )

  def routeToSwitchLanguage: String => Call =
    (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)

  lazy val removeTaxLiabilityOnTaskList : Boolean =
    configuration.get[Boolean]("microservice.services.features.removeTaxLiabilityOnTaskList")

  lazy val enableWhitelist : Boolean = configuration.get[String]("microservice.services.features.whitelist.enabled").toBoolean

  lazy val campaignWhitelistEnabled : Boolean = configuration.get[Boolean]("microservice.services.features.campaignWhitelist.enabled")
  lazy val auditSubmissions : Boolean =
    configuration.get[Boolean]("microservice.services.features.auditing.submissions.enabled")

  lazy val auditCannotCreateRegistration : Boolean =
    configuration.get[Boolean]("microservice.services.features.auditing.cannotCreateRegistration.enabled")

  lazy val livingSettlorBusinessEnabled : Boolean = configuration.get[Boolean]("microservice.services.features.journey.livingSettlorBusiness.enabled")

  lazy val variationsEnabled: Boolean = configuration.get[Boolean]("microservice.services.features.variations")
}
