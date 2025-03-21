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

package forms.annualallowance.taxyear

import forms.mappings.Mappings
import play.api.data.Form

import javax.inject.Inject

class HowMuchAAChargeYouPaidFormProvider @Inject() extends Mappings {

  def apply(startEndDate: String): Form[BigInt] =
    Form(
      "value" -> bigInt(
        "howMuchAAChargeYouPaid.error.required",
        "howMuchAAChargeYouPaid.error.wholeNumber",
        "howMuchAAChargeYouPaid.error.nonNumeric",
        Seq(startEndDate)
      )
        .verifying(
          inRangeWithArg[BigInt](1, BigInt("999999999"), "howMuchAAChargeYouPaid.error.outOfRange", startEndDate)
        )
    )
}
