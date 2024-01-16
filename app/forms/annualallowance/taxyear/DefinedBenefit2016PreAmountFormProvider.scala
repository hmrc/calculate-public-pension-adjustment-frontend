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

class DefinedBenefit2016PreAmountFormProvider @Inject() extends Mappings {

  def apply(): Form[BigInt] =
    Form(
      "value" -> bigInt(
        "definedBenefit2016PreAmount.error.required",
        "definedBenefit2016PreAmount.error.wholeNumber",
        "definedBenefit2016PreAmount.error.nonNumeric"
      )
        .verifying(
          minimumValue[BigInt](0, "definedBenefit2016PreAmount.error.minimum"),
          maximumValue[BigInt](BigInt("999999999"), "definedBenefit2016PreAmount.error.maximum")
        )
    )
}
