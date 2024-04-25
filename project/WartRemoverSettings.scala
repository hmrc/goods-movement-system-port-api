import play.sbt.routes.RoutesKeys.routes
import sbt.*
import sbt.Keys.*
import wartremover.WartRemover.autoImport._
import wartremover.Warts
import wartremover.Wart.*

object WartRemoverSettings {

  lazy val settings: Seq[Def.Setting[_]] = {

    val warnings = {
      (Compile / compile / wartremoverWarnings) ++= Seq(
        Throw,
        Equals
      )
    }

    val compileErrors = {
      Compile / compile / wartremoverErrors ++= Warts.allBut(
        Seq(
          Throw,
          ToString,
          ImplicitParameter,
          PublicInference,
          Equals,
          Overloading,
          FinalCaseClass,
          NonUnitStatements,
          Any,
          StringPlusAny,
          Nothing,
          PlatformDefault,
          ListAppend,
          ListUnapply
        ): _*
      )
    }

    val routesAndFoldersExclusions = wartremoverExcluded ++= (Compile / routes).value

    val testExclusions = {
      val errorsExcluded = (Test / compile / wartremoverErrors) --= Seq(
        DefaultArguments,
        Serializable,
        Product,
        Any,
        OptionPartial,
        GlobalExecutionContext)

      val warningsExcluded = (Test / compile / wartremoverWarnings) --= Seq(
        Nothing,
        Throw,
        Equals
      )
      errorsExcluded ++ warningsExcluded
    }

    val itTestExclusions = IntegrationTest / compile / wartremoverWarnings --= Seq(Nothing, Throw, Equals)

    warnings ++ compileErrors ++ routesAndFoldersExclusions ++ testExclusions ++ itTestExclusions
  }
}
