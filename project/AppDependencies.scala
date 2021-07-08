import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "org.reactivemongo" %% "play2-reactivemongo"            % "0.20.13-play27",
    "uk.gov.hmrc"       %% "play-health"                    % "3.16.0-play-27",
    "uk.gov.hmrc"       %% "play-frontend-hmrc"             % "0.79.0-play-27",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.6.0-play-26",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-27"     % "5.3.0",
    "uk.gov.hmrc"       %% "domain"                         % "5.10.0-play-27",
    "com.typesafe.play" %% "play-json-joda"                 % "2.7.4",
    "org.typelevel"     %% "cats-core"                      % "2.0.0",
    "uk.gov.hmrc"       %% "tax-year"                       % "1.1.0",
    "uk.gov.hmrc"       %% "play-language"                  % "5.0.0-play-27"
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
    "com.github.tomakehurst"   % "wiremock-standalone"    % "2.27.2"
  ).map(_ % "test")

  val it: Seq[ModuleID] = Seq(
    "org.scalatestplus.play"  %% "scalatestplus-play"     % "4.0.3",
    "uk.gov.hmrc"             %% "hmrctest"               % "3.9.0-play-26"
  ).map(_ % "it")

  def apply(): Seq[ModuleID] = compile ++ test ++ it

  val akkaVersion = "2.6.7"
  val akkaHttpVersion = "10.1.12"

  val overrides = Seq(
    "com.typesafe.akka" %% "akka-stream_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core_2.12" % akkaHttpVersion
  )
}