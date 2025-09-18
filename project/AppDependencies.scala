import sbt.*

object AppDependencies {

  private val bootstrapPlay30Version = "9.16.0"
  private val playVersion            = "play-30"

  val compile = Seq(
    "uk.gov.hmrc"   %% s"bootstrap-backend-$playVersion" % bootstrapPlay30Version,
    "org.typelevel" %% "cats-core"                       % "2.12.0"
  )

  val test = Seq(
    "uk.gov.hmrc"                  %% s"bootstrap-test-$playVersion" % bootstrapPlay30Version % "test, it",
    "org.mockito"                   % "mockito-core"                 % "5.1.1"                % "test, it",
    "com.github.java-json-tools"    % "json-schema-validator"        % "2.2.14"               % "it",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"         % "2.17.2"               % "it"
  )
}
