/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.utils.query

import cats.implicits._
import play.api.mvc.QueryStringBindable

import java.time.{Instant, ZonedDateTime}
import scala.util.Try

object InstantBindableUtil {

  implicit val instantBindable = new QueryStringBindable[Instant] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Instant]] =
      params
        .get(key)
        .flatMap(_.headOption)
        .map(str => Try(ZonedDateTime.parse(str).toInstant).toEither.leftMap(e => s"Could not parse $key ('$str')"))

    override def unbind(key: String, value: Instant): String =
      s"$key=${value.toString}"
  }

}
