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
import play.api.i18n.Messages

import javax.inject.Inject

class AmountSalarySacrificeArrangementsFormProvider @Inject() extends Mappings {

  def apply(startEndDate: String)(implicit messages: Messages): Form[BigInt] =
    Form(
      "value" -> bigInt(
        messages("amountSalarySacrificeArrangements.error.required", startEndDate),
        messages("amountSalarySacrificeArrangements.error.wholeNumber", startEndDate),
        messages("amountSalarySacrificeArrangements.error.nonNumeric", startEndDate)
      )
        .verifying(inRange[BigInt](0, BigInt("999999999"), messages("amountSalarySacrificeArrangements.error.outOfRange",startEndDate)))
    )
}
