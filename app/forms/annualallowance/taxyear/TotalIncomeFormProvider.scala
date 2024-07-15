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
import play.api.i18n.Messages

import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class TotalIncomeFormProvider @Inject() extends Mappings {

  def apply(startEndDate: String): Form[BigInt] =
    Form(
      "value" -> bigInt(
        "totalIncome.error.required",
        "totalIncome.error.wholeNumber",
        "totalIncome.error.nonNumeric",
        Seq(startEndDate)
      )
        .verifying(
          minimumValue[BigInt](
            BigInt("0"),
            "totalIncome.error.minimum",
            startEndDate
          ),
          maximumValue[BigInt](
            BigInt("999999999"),
            "totalIncome.error.maximum",
            startEndDate
          )
        )
    )

}
