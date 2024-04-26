import sbt._

object AppDependencies {

  val playVersion =       "play-30"
  val bootstrapVersion =  "8.5.0"

  val compile = Seq(
    "uk.gov.hmrc"       %% "sca-wrapper-play-30"                % "1.7.0"
  )

  val test = Seq(
    "uk.gov.hmrc"       %% s"bootstrap-test-$playVersion"       % bootstrapVersion
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
