import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "play-frontend-hmrc"             % "3.22.0-play-28",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.11.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"     % "5.24.0",
    "uk.gov.hmrc"       %% "play-language"                  % "5.3.0-play-28",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"             % "0.68.0",
    "uk.gov.hmrc"       %% "domain"                         % s"8.0.0-play-28"
  )

  val test = Seq(
    "org.scalatest"           %% "scalatest"               % "3.2.12",
    "org.scalatestplus"       %% "scalacheck-1-15"         % "3.2.11.0",
    "org.scalatestplus"       %% "mockito-3-4"             % "3.2.10.0",
    "org.scalatestplus.play"  %% "scalatestplus-play"      % "5.1.0",
    "org.pegdown"             %  "pegdown"                 % "1.6.0",
    "org.jsoup"               %  "jsoup"                   % "1.15.2",
    "com.typesafe.play"       %% "play-test"               % PlayVersion.current,
    "org.mockito"             %% "mockito-scala"           % "1.17.7",
    "org.scalacheck"          %% "scalacheck"              % "1.16.0",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28" % "0.68.0",
    "com.vladsch.flexmark"    %  "flexmark-all"            % "0.62.2"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
