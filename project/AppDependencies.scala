import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "org.reactivemongo" %% "play2-reactivemongo"            % "0.18.3-play26",
    "uk.gov.hmrc"       %% "logback-json-logger"            % "4.6.0",
    "uk.gov.hmrc"       %% "govuk-template"                 % "5.55.0-play-26",
    "uk.gov.hmrc"       %% "play-health"                    % "3.14.0-play-26",
    "uk.gov.hmrc"       %% "play-ui"                        % "8.5.0-play-26",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.2.0-play-26",
    "uk.gov.hmrc"       %% "bootstrap-play-26"              % "1.8.0",
    "uk.gov.hmrc"       %% "play-whitelist-filter"          % "3.1.0-play-26",
    "uk.gov.hmrc"       %% "domain"                         % "5.9.0-play-26",
    "com.typesafe.play" %% "play-json-joda"                 % "2.7.4",
    "org.typelevel"     %% "cats-core"                      % "2.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.pegdown"              % "pegdown"                % "1.6.0",
    "org.scalatest"           %% "scalatest"              % "3.0.4",
    "org.scalatestplus.play"  %% "scalatestplus-play"     % "3.1.2",
    "uk.gov.hmrc"             %% "hmrctest"               % "3.9.0-play-26",
    "org.jsoup"                % "jsoup"                  % "1.12.1",
    "com.typesafe.play"       %% "play-test"              % PlayVersion.current,
    "org.mockito"              % "mockito-all"            % "1.10.19",
    "org.scalacheck"          %% "scalacheck"             % "1.14.3",
    "wolfendale"              %% "scalacheck-gen-regexp"  % "0.1.2",
    "com.github.tomakehurst"   % "wiremock-standalone"    % "2.25.1"
  ).map(_ % "test")

  val it: Seq[ModuleID] = Seq(
    "org.scalatestplus.play"  %% "scalatestplus-play"     % "3.1.2",
    "uk.gov.hmrc"             %% "hmrctest"               % "3.9.0-play-26"
  ).map(_ % "it")

  def apply(): Seq[ModuleID] = compile ++ test ++ it
}