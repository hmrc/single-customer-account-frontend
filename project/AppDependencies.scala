import sbt.*

object AppDependencies {

  val playVersion = "play-30"
  val bootstrapVersion = "9.12.0"

  val compile = Seq(
    "uk.gov.hmrc" %% s"sca-wrapper-$playVersion" % "2.10.0"
  )

  val test = Seq(
    "uk.gov.hmrc" %% s"bootstrap-test-$playVersion" % bootstrapVersion
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
