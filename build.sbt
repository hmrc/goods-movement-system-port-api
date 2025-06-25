import com.typesafe.sbt.web.PathMapping
import com.typesafe.sbt.web.pipeline.Pipeline
import play.sbt.PlayImport.PlayKeys.playDefaultPort
import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import sbt.Keys.evictionErrorLevel

val appName = "goods-movement-system-port-api"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    majorVersion := 0,
    scalaVersion := "3.6.4",
    scalafmtOnCompile := true,
    scalacOptions ++= Seq(
      "-language:postfixOps",
      "-Wconf:src=routes/.*:s", // Silence all warnings in generated routes
      "-no-indent"
    )
  )
  .settings(CodeCoverageSettings.settings)
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    playDefaultPort := 8988,
    routesImport ++= Seq(
      "java.time.Instant",
      "uk.gov.hmrc.goodsmovementsystemportapi.utils.query.InstantBindableUtil._"
    )
  )
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(
    Compile / unmanagedResourceDirectories += baseDirectory.value / "resources",
    IntegrationTest / unmanagedSourceDirectories :=
      (IntegrationTest / baseDirectory)(base => Seq(base / "it", base / "test-common")).value,
    Test / unmanagedSourceDirectories := (Test / baseDirectory)(base => Seq(base / "test", base / "test-common")).value,
    IntegrationTest / unmanagedResourceDirectories := Seq(baseDirectory.value / "test-resources"),
    Test / unmanagedResourceDirectories := Seq(baseDirectory.value / "test-resources"),
    IntegrationTest / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports/html-it-report")
  )
  .disablePlugins(JUnitXmlReportPlugin)

evictionErrorLevel := Level.Warn

// Task to create a ZIP file containing all json schemas for each version, under the version directory
val zipSchemas = taskKey[Pipeline.Stage]("Zips up all JSON schemas")
zipSchemas := { mappings: Seq[PathMapping] =>
  val targetDir = WebKeys.webTarget.value / "zip"
  val zipFiles: Iterable[java.io.File] =
    (baseDirectory.value / "resources" / "public" / "api" / "conf").listFiles
      .filter(_.isDirectory)
      .map { dir =>
        val schemaPaths  = Path.allSubpaths(dir / "schemas")
        val examplePaths = Path.allSubpaths(dir / "examples")
        val zipFile      = targetDir / "api" / "conf" / dir.getName / "gmvs-port-schemas.zip"
        IO.zip(schemaPaths ++ examplePaths, zipFile)

        zipFile
      }
  zipFiles.pair(Path.relativeTo(targetDir)) ++ mappings
}
pipelineStages := Seq(zipSchemas)
