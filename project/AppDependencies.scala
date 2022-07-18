import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "org.reactivemongo" %% "play2-reactivemongo"            % "0.20.13-play28",
    "uk.gov.hmrc"       %% "play-frontend-hmrc"             % "3.22.0-play-28",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.11.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"     % "6.3.0",
    "uk.gov.hmrc"       %% "domain"                         % "8.1.0-play-28",
    "com.typesafe.play" %% "play-json-joda"                 % "2.9.2",
    "org.typelevel"     %% "cats-core"                      % "2.8.0",
    "uk.gov.hmrc"       %% "tax-year"                       % "3.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.pegdown"              % "pegdown"                % "1.6.0",
    "org.scalatest"           %% "scalatest"              % "3.2.12",
    "org.scalatestplus.play"  %% "scalatestplus-play"     % "5.1.0",
    "org.jsoup"                % "jsoup"                  % "1.15.2",
    "com.typesafe.play"       %% "play-test"              % PlayVersion.current,
    "org.mockito"              % "mockito-all"            % "1.10.19",
    "org.scalatestplus"       %% "mockito-4-5"            % "3.2.12.0",
    "org.scalatestplus"       %% "scalacheck-1-16"        % "3.2.12.0",
    "wolfendale"              %% "scalacheck-gen-regexp"  % "0.1.2",
    "com.github.tomakehurst"   % "wiremock-standalone"    % "2.27.2",
    "com.vladsch.flexmark"     % "flexmark-all"           % "0.62.2"
  ).map(_ % "test")

  val it: Seq[ModuleID] = Seq(
    "org.scalatest"           %% "scalatest"              % "3.2.12",
    "org.scalatestplus.play"  %% "scalatestplus-play"     % "5.1.0",
    "org.pegdown"              % "pegdown"                % "1.6.0",
    "com.vladsch.flexmark"     % "flexmark-all"           % "0.62.2"
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
