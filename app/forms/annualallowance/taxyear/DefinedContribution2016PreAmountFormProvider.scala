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

class DefinedContribution2016PreAmountFormProvider @Inject() extends Mappings {

  def apply(args: Seq[String]): Form[BigInt] =
    Form(
      "value" -> bigInt(
        "definedContribution2016PreAmount.error.required",
        "definedContribution2016PreAmount.error.wholeNumber",
        "definedContribution2016PreAmount.error.nonNumeric",
        args
      )
        .verifying(
          minimumValue[BigInt](0, "definedContribution2016PreAmount.error.minimum", args(0)),
          maximumValue[BigInt](BigInt("999999999"), "definedContribution2016PreAmount.error.maximum", args(0))
        )
    )
}
