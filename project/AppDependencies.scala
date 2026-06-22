import sbt.*

object AppDependencies {

  val playVersion = "play-30"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% s"sca-wrapper-$playVersion" % "5.3.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% s"sca-wrapper-test-$playVersion" % "5.3.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
