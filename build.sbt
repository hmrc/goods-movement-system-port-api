import com.typesafe.sbt.web.PathMapping
import com.typesafe.sbt.web.pipeline.Pipeline
import play.sbt.PlayImport.PlayKeys.playDefaultPort
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import wartremover.Wart._
import sbt.Keys.evictionErrorLevel

val appName = "goods-movement-system-port-api"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    majorVersion := 0,
    scalaVersion := "2.13.12",
    scalacOptions ++= Seq(
      "-Wconf:src=routes/.*:s", //Silence all warnings in generated routes
      "-Ymacro-annotations",
    )
  )
  .settings( //fix scaladoc generation in jenkins
    Compile / scalacOptions -= "utf8",
    scalacOptions += "-language:postfixOps")
  .settings(
    ScoverageKeys.coverageExcludedFiles :=
      "<empty>;com.kenshoo.play.metrics.*;.*definition.*;prod.*;testOnlyDoNotUseInAppConf.*;" +
        "app.*;.*BuildInfo.*;.*Routes.*;.*repositories.*;.*controllers.test.*;.*services.test.*;.*metrics.*",
    ScoverageKeys.coverageMinimumStmtTotal := 80,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    playDefaultPort := 8988
  )
  .settings(
    routesImport ++= Seq(
      "java.time.Instant",
      "uk.gov.hmrc.goodsmovementsystemportapi.utils.query.InstantBindableUtil._"
    )
  )
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(resolvers += "emueller-bintray" at "https://dl.bintray.com/emueller/maven")
  .settings(
    Compile / unmanagedResourceDirectories += baseDirectory.value / "resources",
    IntegrationTest / unmanagedSourceDirectories :=
      (IntegrationTest / baseDirectory)(base => Seq(base / "it", base / "test-common")).value,
    Test / unmanagedSourceDirectories := (Test / baseDirectory)(base => Seq(base / "test", base / "test-common")).value,
    IntegrationTest / unmanagedResourceDirectories := Seq(baseDirectory.value / "test-resources"),
    Test / unmanagedResourceDirectories := Seq(baseDirectory.value / "test-resources"),
    IntegrationTest / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports/html-it-report")
  )
  .settings(
    wartremover.WartRemover.autoImport.wartremoverExcluded ++= (Compile / routes).value,
    Compile / compile / wartremoverErrors ++= Warts.allBut(
      Seq(
        Wart.Throw,
        Wart.ToString,
        Wart.ImplicitParameter,
        Wart.PublicInference,
        Wart.Equals,
        Wart.Overloading,
        Wart.FinalCaseClass,
        Wart.Nothing,
        Wart.NonUnitStatements,
        Any,
        StringPlusAny,
        Nothing,
        PlatformDefault,
        ListAppend,
        ListUnapply,
      ): _*
    ),
    Test / compile / wartremoverErrors --= Seq(DefaultArguments, Serializable, Product, Any, OptionPartial, GlobalExecutionContext),
    Compile / compile / wartremoverWarnings ++= Seq(Wart.Nothing, Wart.Throw, Wart.Equals),
    Test / compile / wartremoverWarnings --= Seq(Nothing, Throw, Equals),
    IntegrationTest / compile / wartremoverWarnings --= Seq(Nothing, Throw, Equals)
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
