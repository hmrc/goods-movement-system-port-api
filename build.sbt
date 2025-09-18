import com.typesafe.sbt.web.PathMapping
import com.typesafe.sbt.web.pipeline.Pipeline
import play.sbt.PlayImport.PlayKeys.playDefaultPort
import sbt.Keys.evictionErrorLevel
import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings

import java.nio.file.Files

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

  val targetDir = WebKeys.webTarget.value / "zip"
  val resDir = baseDirectory.value / "resources" / "public" / "api" / "conf"

  val zipFiles = resDir
    .listFiles()
    .filter(_.isDirectory)
    .map { dir =>
      val schemaPaths  = Path.allSubpaths(dir / "schemas")
      val examplePaths = Path.allSubpaths(dir / "examples")
      val propertiesFile = (dir / "common" / "schemas" / "properties.json")
      val commonFile =
        if (propertiesFile.exists())
          Seq((propertiesFile, "gmr-types-schema.json"))
        else Seq.empty

      val schemaFiles = schemaPaths.collect {
        case (file, _) =>
          val content = IO.read(file)
          val updatedContent = content.replace("../common/schemas/properties.json", "./gmr-types-schema.json")
          val tempFile = Files.createTempFile("schema-", ".json").toFile
          IO.write(tempFile, updatedContent)

          val name = file.getName.stripSuffix(".json") + "_schema.json"
          (tempFile, name)
      }.toSeq

      val exampleFiles = examplePaths.collect {
        case (file, _) =>
          val name = file.getName.stripSuffix(".json") + "_example.json"
          (file, name)
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
