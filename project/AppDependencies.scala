import sbt.*

object AppDependencies {

  private val bootstrapVersion = "9.0.0"
  private val mongoVersion = "2.1.0"

  private val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30"             % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"                     % mongoVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30"             % "9.11.0",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping-play-30"  % "3.0.0",
    "uk.gov.hmrc"       %% "domain-play-30"                         % "9.0.0",
    "uk.gov.hmrc"       %% "tax-year"                               % "5.0.0",
    "org.typelevel"     %% "cats-core"                              % "2.12.0"
  )

  private val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"   % bootstrapVersion,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-30"  % mongoVersion,
    "org.jsoup"               %  "jsoup"                    % "1.18.1",
    "org.mockito"             %% "mockito-scala-scalatest"  % "1.17.37",
    "org.scalatestplus"       %% "scalacheck-1-17"          % "3.2.18.0",
    "io.github.wolfendale"    %% "scalacheck-gen-regexp"    % "1.1.0"
  ).map(_ % Test)


  def apply(): Seq[ModuleID] = compile ++ test

}
