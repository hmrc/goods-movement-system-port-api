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
