import sbt._

object AppDependencies {

  val playVersion =       "play-30"
  val bootstrapVersion =  "8.1.0"

  val compile = Seq(
    "uk.gov.hmrc"       %% s"bootstrap-frontend-$playVersion"   % bootstrapVersion,
    "uk.gov.hmrc"       %% "sca-wrapper-play-30"                % "1.1.0-SNAPSHOT"
  )

  val test = Seq(
    "uk.gov.hmrc"       %% s"bootstrap-test-$playVersion"       % bootstrapVersion
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
