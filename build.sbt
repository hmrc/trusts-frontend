import play.sbt.routes.RoutesKeys
import sbt.Def
import uk.gov.hmrc.DefaultBuildSettings.*

lazy val appName: String = "trusts-frontend"

ThisBuild / scalaVersion := "3.9.0"
ThisBuild / majorVersion := 2
ThisBuild / targetJvm := "jvm-21"

lazy val microservice = (project in file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) // Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(inConfig(Test)(testSettings))
  .settings(
    name := appName,
    RoutesKeys.routesImport += "models.*",
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "play.twirl.api.HtmlFormat.*",
      "uk.gov.hmrc.govukfrontend.views.html.components.*",
      "uk.gov.hmrc.hmrcfrontend.views.html.components.*",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers.*",
      "views.ViewUtils.*",
      "models.Mode",
      "controllers.routes.*"
    ),
    PlayKeys.playDefaultPort := 9781,
    CodeCoverageSettings(),
    scalacOptions ++= Seq(
      "-feature",
      "-Wconf:src=routes/.*:s",
      "-Wconf:msg=unused import&src=html/.*:s",
      "-Wconf:msg=Flag.*repeatedly:s",
      "-Wconf:msg=is deprecated for wildcard arguments of types:s"
    ),
    libraryDependencies ++= AppDependencies(),
    retrieveManaged := true
  )

lazy val testSettings: Seq[Def.Setting[?]] = Seq(
  fork := true,
  javaOptions ++= Seq(
    "-Dlogger.resource=logback-test.xml",
    "-Dconfig.resource=test.application.conf"
  )
)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(itSettings())

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt it/Test/scalafmt")
