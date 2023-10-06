/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.helpers

import akka.actor.ActorSystem
import akka.stream.Materializer
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{Inside, Status => _}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.{HeaderNames, Status, Writeable}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc._
import play.api.test.{DefaultAwaitTimeout, FakeRequest, ResultExtractors, RouteInvokers, Writeables}
import play.api.{Application, Mode}
import uk.gov.hmrc.goodsmovementsystemportapi.{FakeObjects, ResultAssertions}

import scala.concurrent.ExecutionContext.global
import scala.concurrent.{ExecutionContext, Future}

abstract class BaseISpec
    extends AnyWordSpec
    with GuiceOneAppPerSuite
    with AdditionalAppConfig
    with WireMockHelper
    with ScalaFutures
    with DefaultAwaitTimeout
    with Matchers
    with Writeables
    with Inside
    with HeaderNames
    with RouteInvokers
    with ResultExtractors
    with ResultAssertions
    with WireMockSupport
    with WireMockConfig
    with Results
    with Status
    with FakeObjects {

  implicit lazy val system:       ActorSystem      = ActorSystem()
  implicit lazy val materializer: Materializer     = Materializer(system)
  implicit def ec:                ExecutionContext = global

  val acceptHeader: (String, String) = "Accept" -> "application/vnd.hmrc.1.0+json"

  def fakeRequest(call: Call): FakeRequest[AnyContentAsJson] =
    FakeRequest(call)
      .withHeaders(acceptHeader, HeaderNames.AUTHORIZATION -> "Bearer testBearer")
      .withJsonBody(Json.parse("{}"))

  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = Span(10, Seconds), interval = Span(50, Millis))

  additionalAppConfig += (
    "auditing.enabled" -> false
  )

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .disable[com.kenshoo.play.metrics.PlayModule]
      .configure(additionalAppConfig.toMap[String, Any])
      .in(Mode.Test)
      .build()

  def callRoute[A](req: Request[A])(implicit app: Application, w: Writeable[A]): Future[Result] = {
    val errorHandler = app.errorHandler

    route(app, req) match {
      case None => fail("Route does not exist")
      case Some(fResult) =>
        fResult.recoverWith {
          case t: Throwable => errorHandler.onServerError(req, t)
        }
    }
  }
}
