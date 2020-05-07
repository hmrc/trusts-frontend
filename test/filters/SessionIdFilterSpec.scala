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

package filters

import java.util.UUID

import akka.stream.Materializer
import com.google.inject.Inject
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.{DefaultActionBuilder, Results, SessionCookieBaker}
import play.api.routing.Router
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.{HeaderNames, SessionKeys}

import scala.concurrent.ExecutionContext

object SessionIdFilterSpec {

  val sessionId = "28836767-a008-46be-ac18-695ab140e705"

  class TestSessionIdFilter @Inject()(
                                       override val mat: Materializer,
                                       sessionCookieBaker: SessionCookieBaker,
                                       ec: ExecutionContext
                                     ) extends SessionIdFilter(mat, UUID.fromString(sessionId), sessionCookieBaker, ec)

}

class TrustRouter @Inject()(actionBuilder : DefaultActionBuilder) {

  def router: Router = {

    import play.api.routing.sird._

    Router.from {
      case GET(p"/test") => actionBuilder.apply {
        request =>
          val fromHeader = request.headers.get(HeaderNames.xSessionId).getOrElse("")
          val fromSession = request.session.get(SessionKeys.sessionId).getOrElse("")
          Results.Ok(
            Json.obj(
              "fromHeader" -> fromHeader,
              "fromSession" -> fromSession
            )
          )
      }
      case GET(p"/test2") => actionBuilder.apply {
        implicit request =>
          Results.Ok.addingToSession("foo" -> "bar")
      }
    }
  }

}

class SessionIdFilterSpec extends WordSpec with MustMatchers with MockitoSugar with GuiceOneAppPerSuite with OptionValues {

  import SessionIdFilterSpec._

  val registrationsRepository = mock[RegistrationsRepository]

  "session id filter" must {

    "add a sessionId if one doesn't already exist" in {
      val application = createApp()

      val result = route(application, FakeRequest(GET, "/test")).value

      val body = contentAsJson(result)

      (body \ "fromHeader").as[String] mustEqual s"session-$sessionId"
      (body \ "fromSession").as[String] mustEqual s"session-$sessionId"

      session(result).data.get(SessionKeys.sessionId) mustBe defined

      application.stop()
    }

    "not override a sessionId if one doesn't already exist" in {
      val application = createApp()

      val result = route(application, FakeRequest(GET, "/test").withSession(SessionKeys.sessionId -> "foo")).value

      val body = contentAsJson(result)

      (body \ "fromHeader").as[String] mustEqual ""
      (body \ "fromSession").as[String] mustEqual "foo"

      application.stop()
    }

    "not override other session values from the response" in {
      val application = createApp()

      val result = route(application, FakeRequest(GET, "/test2")).value

      session(result).data must contain("foo" -> "bar")

      application.stop()
    }

    "not override other session values from the request" in {
      val application = createApp()

      val result = route(application, FakeRequest(GET, "/test").withSession("foo" -> "bar")).value
      session(result).data must contain("foo" -> "bar")

      application.stop()
    }
  }

  private def createApp() = {
    new GuiceApplicationBuilder()
      .overrides(
        bind[SessionIdFilter].to[TestSessionIdFilter],
        bind[RegistrationsRepository].toInstance(registrationsRepository)
      )
      .configure(
        "play.filters.disabled" -> List("uk.gov.hmrc.play.bootstrap.filters.frontend.crypto.SessionCookieCryptoFilter")
      )
      .router(app.injector.instanceOf[TrustRouter].router)
      .build()
  }
}
