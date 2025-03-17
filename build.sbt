import play.sbt.routes.RoutesKeys
import sbt.Def
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.{itSettings, targetJvm}
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

lazy val appName: String = "calculate-public-pension-adjustment-frontend"

val scala3_3_4 = "3.3.4"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := scala3_3_4

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(root % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(itSettings())
  .settings(libraryDependencies ++= AppDependencies.itDependencies)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin, AutomateHeaderPlugin, BuildInfoPlugin)
  .disablePlugins(JUnitXmlReportPlugin) // Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(inConfig(Test)(testSettings): _*)
  .settings(majorVersion := 0)
  .settings(ThisBuild / useSuperShell := false)
  .settings(
    scalaVersion := "3.3.4",
    name := appName,
    RoutesKeys.routesImport ++= Seq(
      "models._",
      "uk.gov.hmrc.play.bootstrap.binders.RedirectUrl"
    ),
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "play.twirl.api.HtmlFormat._",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._",
      "uk.gov.hmrc.hmrcfrontend.views.config._",
      "views.ViewUtils._",
      "models.Mode",
      "controllers.routes._",
      "viewmodels.govuk.all._",
      "utils.RequestOps._"
    ),
    PlayKeys.playDefaultPort := 12804,
    ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*handlers.*;.*components.*;" +
      ".*Routes.*;.*viewmodels.govuk.*;",
    ScoverageKeys.coverageMinimumStmtTotal := 80,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    scalacOptions ++= Seq(
      "-Wconf:src=routes/.*:s",
      "-Wconf:msg=unused.import&src=html/.*:s",
      "-Wconf:msg=unused.explicit.parameter&src=html/.*:s",
      "-Wconf:msg=unused.import&src=xml/.*:s",
      "-Wconf:msg=Flag.*repeatedly:s",
      "-feature",
      "-deprecation",
      "-Ypatmat-exhaust-depth",
      "off"
    ),
    libraryDependencies ++= AppDependencies(),
    retrieveManaged := true,
    resolvers ++= Seq(Resolver.jcenterRepo),
    // concatenate js
    Concat.groups := Seq(
      "javascripts/application.js" ->
        group(
          Seq(
            "javascripts/app.js"
          )
        )
    ),
    // prevent removal of unused code which generates warning errors due to use of third-party libs
    uglifyCompressOptions := Seq("unused=false", "dead_code=false"),
    uglifyOps := UglifyOps.singleFile,
    pipelineStages := Seq(digest),
    // below line required to force asset pipeline to operate in dev rather than only prod
    Assets / pipelineStages := Seq(concat, uglify),
    // only compress files generated by concat
    uglify / includeFilter := GlobFilter("application.js")
  )

lazy val testSettings: Seq[Def.Setting[_]] = Seq(
  fork := true,
  unmanagedSourceDirectories += baseDirectory.value / "test-utils"
)
