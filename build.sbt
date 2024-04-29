import play.sbt.routes.RoutesKeys
import sbt.Def
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings.scalaSettings
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

lazy val appName: String = "single-customer-account-frontend"

val scala2_13 = "2.13.12"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := scala2_13
ThisBuild / scalafmtOnCompile := true

lazy val scoverageSettings = {
  import scoverage.ScoverageKeys
  Seq(
    ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;models/.data/..*;view.*;models.*;uk.gov.hmrc.play.config.*;.*(BuildInfo|Routes).*;config/*",
    ScoverageKeys.coverageMinimumStmtTotal := 80,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true
  )
}
lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    PlayKeys.playDefaultPort := 9031,
    scoverageSettings,
    scalaSettings,
    libraryDependencies ++= AppDependencies()
  )
  .settings(
    scalacOptions ++= Seq(
      "-unchecked",
      "-feature",
      "-Xlint:_",
      "-Wdead-code",
      "-Wunused:_",
      "-Wextra-implicit",
      "-Wvalue-discard",
      "-Werror",
      "-Wconf:cat=unused-imports&site=.*views\\.html.*:s",
      "-Wconf:cat=unused&src=.*views\\.html.*:s",
      "-Wconf:cat=unused&src=.*Routes\\.scala:s",
      "-Wconf:cat=deprecation&msg=.*method layout in class WrapperService is deprecated.*:s"
    )
  )

Test / Keys.fork := true
Test / parallelExecution := true
Test / scalacOptions --= Seq("-Wdead-code", "-Wvalue-discard")

TwirlKeys.templateImports ++= Seq(
  "uk.gov.hmrc.govukfrontend.views.html.components._",
  "uk.gov.hmrc.hmrcfrontend.views.html.components._",
  "uk.gov.hmrc.hmrcfrontend.views.html.helpers._"
)
