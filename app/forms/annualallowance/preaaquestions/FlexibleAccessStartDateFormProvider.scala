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

package forms.annualallowance.preaaquestions

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class FlexibleAccessStartDateFormProvider @Inject() extends Mappings {

  def apply(): Form[LocalDate] = {

    val min                               = LocalDate.of(2015, 4, 6)
    val max                               = LocalDate.of(2023, 4, 5)
    val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

    Form(
      "value" -> localDate(
        invalidKey = "flexibleAccessStartDate.error.invalid",
        allRequiredKey = "flexibleAccessStartDate.error.required.all",
        twoRequiredKey = "flexibleAccessStartDate.error.required.two",
        requiredKey = "flexibleAccessStartDate.error.required"
      )
        .verifying(maxDate(max, "flexibleAccessStartDate.error.max", max.format(dateTimeFormat)))
        .verifying(minDate(min, "flexibleAccessStartDate.error.min", min.format(dateTimeFormat)))
    )
  }
}
