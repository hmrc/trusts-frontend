import sbt.Setting
import scoverage.ScoverageKeys
import scoverage.ScoverageKeys.*

object CodeCoverageSettings {

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

  private val settings: Seq[Setting[?]] = Seq(
    coverageExcludedFiles := excludedPackages.mkString(";"),
    coverageMinimumStmtTotal := 80,
    coverageFailOnMinimum := true,
    coverageHighlighting := true
  )

  def apply(): Seq[Setting[?]] = settings
}
