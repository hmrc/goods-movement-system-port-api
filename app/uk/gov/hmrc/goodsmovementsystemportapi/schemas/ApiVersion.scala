/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.schemas

import cats.{Order, Show}

trait ApiVersion {
  def ordinal: Int
}

case object Version_1_0 extends ApiVersion {
  val ordinal = 0
}

object ApiVersion {

  def all: List[ApiVersion] = List(Version_1_0)

  implicit val shows: Show[ApiVersion] = new Show[ApiVersion] {

    override def show(version: ApiVersion): String = version match {
      case Version_1_0 => "1.0"
    }
  }

  def of(s: String): Option[ApiVersion] = s match {
    case "1.0" => Option(Version_1_0)
    case _     => None
  }

  implicit val versionOrder: Order[ApiVersion] = new Order[ApiVersion] {
    def compare(x: ApiVersion, y: ApiVersion): Int = x.ordinal compare y.ordinal
  }

}
