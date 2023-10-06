/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers

sealed trait PortErrors extends Product with Serializable

sealed trait GetDepartureErrors extends PortErrors

sealed trait GetControlledArrivalErrors extends PortErrors

sealed trait GetControlledDepartureErrors extends PortErrors

sealed trait SubscriptionValidationErrors extends GetDepartureErrors with GetControlledDepartureErrors with GetControlledArrivalErrors

object PortErrors {
  case object SubscriptionPortIdNotFoundError extends SubscriptionValidationErrors
  case object PortIdMismatchError extends SubscriptionValidationErrors
  case object InvalidDateCombinationError extends GetDepartureErrors
  case object TooManyGmrsError extends GetDepartureErrors

  val portIdMismatchError:             SubscriptionValidationErrors = PortIdMismatchError
  val subscriptionPortIdNotFoundError: SubscriptionValidationErrors = SubscriptionPortIdNotFoundError
  val invalidDateCombinationError:     GetDepartureErrors           = InvalidDateCombinationError
  val tooManyGmrsError:                GetDepartureErrors           = TooManyGmrsError
}
