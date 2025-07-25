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

enablePlugins(SbtWeb)
// Task to create a ZIP file containing all json schemas for each version, under the version directory
lazy val zipSchemas = taskKey[Pipeline.Stage]("Zips up all JSON schemas")
zipSchemas := { mappings: Seq[PathMapping] =>
  println("🔥🔥🔥 zipSchemas task is executing!")

  val targetDir = WebKeys.webTarget.value / "zip"
  val resDir = baseDirectory.value / "resources" / "public" / "api" / "conf"
  val propertiesFile = (resDir / "common" / "schemas" / "properties.json")
  val zipFiles = resDir
    .listFiles()
    .filter(_.isDirectory)
    .map { dir =>
      val schemaPaths  = Path.allSubpaths(dir / "schemas")
      val examplePaths = Path.allSubpaths(dir / "examples")

      val commonFile =
        if (propertiesFile.exists())
          Seq((propertiesFile, "schemas/common/gmr-types-schema.json"))
        else Seq.empty

      val schemaFiles = schemaPaths.collect {
        case (file, _) =>
          val name = file.getName.stripSuffix(".json") + "_schema.json"
          (file, s"schemas/$name")
      }.toSeq

      val exampleFiles = examplePaths.collect {
        case (file, _) =>
          val name = file.getName.stripSuffix(".json") + "_example.json"
          (file, s"examples/$name")
      }.toSeq

      val zipFile = targetDir / "api" / "conf" / dir.getName / "gmvs-port-schemas.zip"
      IO.zip(schemaFiles ++ exampleFiles ++ commonFile, zipFile, Some(0L))
      zipFile
    }

  val zipMappings = zipFiles.map { file =>
    (file, s"public/api/conf/${file.getParentFile.getName}/${file.getName}")
  }

  zipMappings.toSeq ++ mappings

}
pipelineStages := Seq(zipSchemas)
