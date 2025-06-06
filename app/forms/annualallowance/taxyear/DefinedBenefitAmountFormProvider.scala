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
import models.Period
import play.api.data.Form

import javax.inject.Inject

class DefinedBenefitAmountFormProvider @Inject() extends Mappings {

  def apply(period: Period): Form[BigInt] =
    Form(
      "value" -> bigInt(
        "definedBenefitAmount.error.required." + period,
        "definedBenefitAmount.error.wholeNumber." + period,
        "definedBenefitAmount.error.nonNumeric." + period
      )
        .verifying(
          minimumValue[BigInt](0, "definedBenefitAmount.error.minimum." + period),
          maximumValue[BigInt](BigInt("999999999"), "definedBenefitAmount.error.maximum." + period)
        )
    )
}
