/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.helpers

import play.api.http.{HeaderNames, Status}
import play.api.mvc.Results
import play.api.test.{DefaultAwaitTimeout, ResultExtractors, StubControllerComponentsFactory}

trait ActionFunctionBaseSpec
    extends BaseSpec
    with Results
    with Status
    with ResultExtractors
    with HeaderNames
    with DefaultAwaitTimeout
    with StubControllerComponentsFactory
