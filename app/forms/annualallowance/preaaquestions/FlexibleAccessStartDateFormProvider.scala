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

import forms.mappings.Mappings
import play.api.data.Form
import play.api.i18n.Messages

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class FlexibleAccessStartDateFormProvider @Inject() extends Mappings {

  private val FLEXIBLE_ACCESS_DATE_MIN_YEAR  = 2015
  private val FLEXIBLE_ACCESS_DATE_MIN_MONTH = 4
  private val FLEXIBLE_ACCESS_DATE_MIN_DAY   = 6

  def apply(flexibleAccessDateMax: LocalDate)(implicit messages: Messages): Form[LocalDate] = {

    val min                               = LocalDate.of(FLEXIBLE_ACCESS_DATE_MIN_YEAR, FLEXIBLE_ACCESS_DATE_MIN_MONTH, FLEXIBLE_ACCESS_DATE_MIN_DAY)
    val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
    Form(
      "value" -> localDate(
        invalidKey = "flexibleAccessStartDate.error.invalid",
        allRequiredKey = "flexibleAccessStartDate.error.required.all",
        twoRequiredKey = "flexibleAccessStartDate.error.required.two",
        requiredKey = "flexibleAccessStartDate.error.required"
      )
        .verifying(
          maxDate(
            flexibleAccessDateMax,
            "flexibleAccessStartDate.error.max",
            flexibleAccessDateMax.plusDays(1).format(dateTimeFormat)
          )
        )
        .verifying(minDate(min, "flexibleAccessStartDate.error.min", min.format(dateTimeFormat)))
    )
  }
}
