import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val bootstrapPlayVersion = "7.15.0"

  val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-28"  % bootstrapPlayVersion,
    "org.typelevel"     %% "cats-core"                  % "2.9.0"
  )

  val test = Seq(
    "uk.gov.hmrc"                   %% "bootstrap-test-play-28" % bootstrapPlayVersion  % "test, it",
    "org.scalatest"                 %% "scalatest"              % "3.2.15"   % "test, it",
    "com.vladsch.flexmark"          % "flexmark-all"            % "0.64.0" % "test, it",
    "org.mockito"                   % "mockito-core"            % "5.1.1"   % "test, it",
    "org.scalatestplus"             %% "mockito-3-4"            % "3.2.10.0" % "test, it",
    "com.typesafe.play"             %% "play-test"              % current   % "test, it",
    "com.github.java-json-tools"    % "json-schema-validator"   % "2.2.14"  % "it",
    "com.fasterxml.jackson.module"  %% "jackson-module-scala"   % "2.12.2"  % "it",
    "org.scalatestplus.play"        %% "scalatestplus-play"     % "5.1.0"   % "test, it",
    "com.github.tomakehurst"        % "wiremock-standalone"     % "2.27.2"  % "test, it"
  )
}
