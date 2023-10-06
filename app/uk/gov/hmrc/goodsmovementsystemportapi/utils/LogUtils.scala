/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.utils

import play.api.Logger
import play.api.mvc.Result
import uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers.ErrorResponse

object LogUtils {
  def logAndResult(error: ErrorResponse, includeMessage: Boolean)(implicit logger: Logger): Result = {
    val maybeMessage = if (includeMessage) s" - ${error.message}" else ""
    logger.info(s"${error.errorCode}$maybeMessage")
    error.toResult
  }
}
