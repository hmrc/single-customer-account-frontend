import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

lazy val appName: String = "single-customer-account-frontend"

ThisBuild / majorVersion := 1
ThisBuild / scalaVersion := "3.3.6"
ThisBuild / scalafmtOnCompile := true
ThisBuild / scalacOptions ++= Seq(
  "-unchecked",
  "-feature",
  "-Wvalue-discard",
//  "-Werror",
  "-Wconf:msg=unused import&src=.*views/.*:s",
  "-Wconf:msg=unused&src=.*RoutesPrefix\\.scala:s",
  "-Wconf:msg=unused&src=.*Routes\\.scala:s",
  "-Wconf:cat=deprecation&msg=.*method layout in class WrapperService is deprecated.*:s",
  "-Wconf:cat=deprecation&msg=.*value name in trait Retrievals is deprecated.*:s",
  "-language:noAutoTupling",
  "-Wvalue-discard",
  "-Wconf:msg=Flag.*repeatedly:s"
)

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
    PlayKeys.playDefaultPort := 8420,
    scoverageSettings,
    libraryDependencies ++= AppDependencies()
  )

Test / Keys.fork := true
Test / parallelExecution := true

TwirlKeys.templateImports ++= Seq(
  "uk.gov.hmrc.govukfrontend.views.html.components._",
  "uk.gov.hmrc.hmrcfrontend.views.html.components._",
  "uk.gov.hmrc.hmrcfrontend.views.html.helpers._"
)
