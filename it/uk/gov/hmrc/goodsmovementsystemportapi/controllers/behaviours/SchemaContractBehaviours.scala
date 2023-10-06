/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.controllers.behaviours

import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.report.{ListReportProvider, LogLevel, ProcessingReport}
import com.github.fge.jsonschema.main.JsonSchemaFactory
import play.api.mvc.Result
import uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers.NestedError
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.{BaseISpec, SchemaFileSearcher}
import uk.gov.hmrc.goodsmovementsystemportapi.schemas.ApiVersion

import scala.collection.JavaConverters._
import scala.concurrent.Future

trait SchemaContractBehaviours {
  this: BaseISpec =>

  private val factory = JsonSchemaFactory
    .newBuilder()
    .setReportProvider(new ListReportProvider(LogLevel.ERROR, LogLevel.FATAL))
    .freeze()

  def validateAgainstSchema(result: Future[Result], schemaName: String, apiVersion: ApiVersion): Unit = {
    val schemaPath = SchemaFileSearcher.getPath(schemaName, apiVersion)

    val schema = factory
      .getJsonSchema(s"resource:$schemaPath")

    val results: ProcessingReport = schema
      .validate(JsonLoader.fromString(contentAsString(result)), true)

    println(handleErrors(results.iterator().asScala.map(_.asJson())))
    results.isSuccess shouldBe true
  }

  private def handleErrors(errors: Iterator[JsonNode]): List[NestedError] = {
    def retrieveNullSafeValue(jsonNode: JsonNode) =
      if (jsonNode.isNull) ""
      else jsonNode.asText
    errors.map { e =>
      NestedError(
        code    = retrieveNullSafeValue(e.get("keyword")).toUpperCase,
        message = retrieveNullSafeValue(e.get("message")).capitalize,
        path = {
          if (e.get("instance").isNull) ""
          else retrieveNullSafeValue(e.get("instance").get("pointer"))
        }
      )
    }.toList
  }
}
