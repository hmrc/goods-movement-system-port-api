/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.goodsmovementsystemportapi.helpers

import java.io.{File, FileNotFoundException}
import cats.implicits._
import org.apache.commons.io.FileUtils
import org.scalatest.exceptions.TestFailedException
import uk.gov.hmrc.goodsmovementsystemportapi.schemas.ApiVersion

import scala.jdk.CollectionConverters._

object SchemaFileSearcher {

  private lazy val files = FileUtils
    .listFiles(new File("resources/public/api/conf"), Array("json"), true)
    .asScala
    .toList

  def getPath(schemaName: String, apiVersion: ApiVersion): String =
    files
      .find(f =>
        f.getName === schemaName
          && f.getPath.contains(apiVersion.show)
          && f.getPath.contains("/schemas")
      )
      .map(_.getPath.replace("resources", ""))
      .getOrElse(
        throw new TestFailedException(
          message = s"$schemaName not found in resources/public/api/conf",
          cause = new FileNotFoundException(schemaName),
          failedCodeStackDepth = 12
        )
      )

}
