/*
 * Copyright 2024 HM Revenue & Customs
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

package models.submission

import play.api.libs.functional.syntax.*
import models.CalculationResults.{CalculationInputs, CalculationResponse}
import play.api.libs.json.{Format, __}

case class SubmissionRequest(
  calculationInputs: CalculationInputs,
  calculation: Option[CalculationResponse],
  userId: String,
  uniqueId: String
)

object SubmissionRequest {

  implicit lazy val format: Format[SubmissionRequest] = (
    (__ \ "calculationInputs").format[CalculationInputs] and
      (__ \ "calculation").formatNullable[CalculationResponse] and
      (__ \ "userId").format[String] and
      (__ \ "uniqueId").format[String]
  )(SubmissionRequest.apply, o => Tuple.fromProductTyped(o))
}
