import play.sbt.routes.RoutesKeys
import sbt.Def
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings

lazy val appName: String = "trusts-frontend"

ThisBuild / scalaVersion := "2.13.16"
ThisBuild / majorVersion := 1

val excludedPackages = Seq(
  "<empty>",
  "Reverse.*",
  ".*assets.*",
  ".*Routes.*",
  ".*standardError.*",
  ".*BuildInfo",
  ".*pages.Page",
  ".*models.*",
  ".*views.*",
  ".*filters.*",
  ".*config*.*",
  ".*testOnlyDoNotUseInAppConf.*"
)

lazy val microservice = (project in file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(inConfig(Test)(testSettings))
  .settings(
    majorVersion := 1,
    name := appName,
    RoutesKeys.routesImport += "models._",
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "play.twirl.api.HtmlFormat._",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._",
      "views.ViewUtils._",
      "models.Mode",
      "controllers.routes._"
    ),
    PlayKeys.playDefaultPort := 9781,
    ScoverageKeys.coverageExcludedFiles := excludedPackages.mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 80,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    scalacOptions ++= Seq(
      "-feature",
      "-Wconf:src=routes/.*:s",
      "-Wconf:cat=unused-imports&src=views/.*:s"
    ),
    libraryDependencies ++= AppDependencies(),
    retrieveManaged := true,
    // concatenate js
    Concat.groups := Seq(
      "javascripts/trustsfrontend-app.js" ->
        group(Seq(
          "javascripts/trustsfrontend.js",
          "javascripts/autocomplete.js",
          "javascripts/iebacklink.js",
          "javascripts/print.js",
          "javascripts/libraries/location-autocomplete.min.js"
        ))
    ),
    // prevent removal of unused code which generates warning errors due to use of third-party libs
    uglifyCompressOptions := Seq("unused=false", "dead_code=false"),
    pipelineStages := Seq(digest),
    uglifyOps               := UglifyOps.singleFile,
    // below line required to force asset pipeline to operate in dev rather than only prod
    Assets / pipelineStages := Seq(concat,uglify),
    // only compress files generated by concat
    uglify / includeFilter := GlobFilter("trustsfrontend-*.js")
  )

lazy val testSettings: Seq[Def.Setting[?]] = Seq(
  fork        := true,
  javaOptions ++= Seq(
    "-Dlogger.resource=logback-test.xml",
    "-Dconfig.resource=test.application.conf"
  )
)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(DefaultBuildSettings.itSettings())

addCommandAlias("scalastyleAll", "all scalastyle Test/scalastyle it/Test/scalastyle")
