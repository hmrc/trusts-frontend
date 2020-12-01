import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "org.reactivemongo" %% "play2-reactivemongo"            % "0.18.3-play27",
    "uk.gov.hmrc"       %% "logback-json-logger"            % "4.8.0",
    "uk.gov.hmrc"       %% "govuk-template"                 % "5.56.0-play-27",
    "uk.gov.hmrc"       %% "play-health"                    % "3.15.0-play-27",
    "uk.gov.hmrc"       %% "play-ui"                        % "8.10.0-play-27",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.4.0-play-26",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-27"     % "2.25.0",
    "uk.gov.hmrc"       %% "domain"                         % "5.10.0-play-27",
    "com.typesafe.play" %% "play-json-joda"                 % "2.7.4",
    "org.typelevel"     %% "cats-core"                      % "2.0.0",
    "uk.gov.hmrc"       %% "tax-year"                       % "1.1.0",
    "uk.gov.hmrc"       %% "play-language"                  % "4.5.0-play-26"
  )

  val test: Seq[ModuleID] = Seq(
    "org.pegdown"              % "pegdown"                % "1.6.0",
    "org.scalatest"           %% "scalatest"              % "3.0.8",
    "org.scalatestplus.play"  %% "scalatestplus-play"     % "4.0.3",
    "uk.gov.hmrc"             %% "hmrctest"               % "3.9.0-play-26",
    "org.jsoup"                % "jsoup"                  % "1.12.1",
    "com.typesafe.play"       %% "play-test"              % PlayVersion.current,
    "org.mockito"              % "mockito-all"            % "1.10.19",
    "org.scalacheck"          %% "scalacheck"             % "1.14.3",
    "wolfendale"              %% "scalacheck-gen-regexp"  % "0.1.2",
    "com.github.tomakehurst"   % "wiremock-standalone"    % "2.25.1"
  ).map(_ % "test")

  val it: Seq[ModuleID] = Seq(
    "org.scalatestplus.play"  %% "scalatestplus-play"     % "3.1.3",
    "uk.gov.hmrc"             %% "hmrctest"               % "3.9.0-play-26"
  ).map(_ % "it")

  def apply(): Seq[ModuleID] = compile ++ test ++ it

  val akkaVersion = "2.6.7"
  val akkaHttpVersion = "10.1.12"

  val overrides = Seq(
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion
  )
}