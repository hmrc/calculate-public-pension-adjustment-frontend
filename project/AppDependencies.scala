import sbt.*

object AppDependencies {
  import play.core.PlayVersion

  private val bootstrapPlay30Version = "10.5.0"
  private val mongoPlay30Version     = "2.12.0"

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30"    % "12.29.0",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping-play-30" % "3.3.0",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30"    % bootstrapPlay30Version,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"            % mongoPlay30Version,
    "uk.gov.hmrc"       %% "crypto-json-play-30"           % "8.4.0"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-30"  % bootstrapPlay30Version % Test,
    "org.scalatestplus"      %% "scalacheck-1-18"         % "3.2.19.0" % Test,
    "org.scalatestplus"      %% "mockito-4-11"            % "3.2.17.0" % Test,
    "org.scalatestplus.play" %% "scalatestplus-play"      % "7.0.2" % Test,
    "org.jsoup"               % "jsoup"                   % "1.21.2" % Test,
    "org.playframework"      %% "play-test"               % PlayVersion.current % Test,
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-30" % mongoPlay30Version % Test,
  )

  val itDependencies = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapPlay30Version % Test
  )

  def apply(): Seq[ModuleID] = compile ++ test
}
