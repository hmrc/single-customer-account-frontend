import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"                 %% s"bootstrap-frontend-play-30"      % "8.1.0",
    "uk.gov.hmrc"       %% "sca-wrapper-play-30"            % "1.1.0-SNAPSHOT"
  )

  val test = Seq(
    "uk.gov.hmrc"                   %% s"bootstrap-test-play-30"        % "8.1.0",

  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
