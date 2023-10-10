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
