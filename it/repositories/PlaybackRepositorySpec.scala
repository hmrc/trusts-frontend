package repositories

import org.scalatest._
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import suite.FailOnUnindexedQueries
import play.api.test.Helpers._

import scala.language.implicitConversions
import scala.concurrent.ExecutionContext.Implicits.global

class PlaybackRepositorySpec extends FreeSpec with MustMatchers with FailOnUnindexedQueries with IntegrationPatience
  with ScalaFutures with OptionValues with Inside with EitherValues with Eventually {

  private lazy val appBuilder: GuiceApplicationBuilder =
    new GuiceApplicationBuilder()

  val json = Json.parse(
    """
      |{
      |  "data" : "lots-of-playback-data",
      |  "responseHeader" : {
      |    "status" : "Processed",
      |    "formBundleNo" : "000000000001"
      |  }
      |}
      |""".stripMargin)

  "a playback repository" - {
    "must be able to store a playback payload for a processed trust" in {

      database.map(_.drop()).futureValue

      val application = appBuilder.build()

      running(application) {

        val repository = application.injector.instanceOf[PlaybackRepository]

        started(application).futureValue

        val storedOk = repository.store("some-internal-id", json)

        storedOk.futureValue mustBe true
      }
    }
  }
}
