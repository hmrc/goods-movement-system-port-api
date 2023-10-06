/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.helpers

import java.io.{File, FileNotFoundException}
import cats.implicits._
import org.apache.commons.io.FileUtils
import org.scalatest.exceptions.TestFailedException
import uk.gov.hmrc.goodsmovementsystemportapi.schemas.ApiVersion

import scala.collection.JavaConverters._

object SchemaFileSearcher {

  private lazy val files = FileUtils
    .listFiles(new File("resources/public/api/conf"), Array("json"), true)
    .asScala
    .toList

  def getPath(schemaName: String, apiVersion: ApiVersion): String =
    files
      .find(f => f.getName === schemaName && f.getPath.contains(apiVersion.show))
      .map(_.getPath.replace("resources", ""))
      .getOrElse(
        throw new TestFailedException(
          message              = s"$schemaName not found in resources/public/api/conf",
          cause                = new FileNotFoundException(schemaName),
          failedCodeStackDepth = 12))

}
