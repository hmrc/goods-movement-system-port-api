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

package uk.gov.hmrc.goodsmovementsystemportapi.utils.query

import cats.implicits._
import play.api.mvc.QueryStringBindable

import java.time.{Instant, ZonedDateTime}
import scala.util.Try

object InstantBindableUtil {

  implicit val instantBindable: QueryStringBindable[Instant] = new QueryStringBindable[Instant] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Instant]] =
      params
        .get(key)
        .flatMap(_.headOption)
        .map(str => Try(ZonedDateTime.parse(str).toInstant).toEither.leftMap(e => s"Could not parse $key ('$str')"))

    override def unbind(key: String, value: Instant): String =
      s"$key=${value.toString}"
  }

}
