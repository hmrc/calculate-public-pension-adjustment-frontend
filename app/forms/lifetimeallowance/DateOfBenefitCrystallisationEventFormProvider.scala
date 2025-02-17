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

package forms.lifetimeallowance

import forms.mappings.Mappings
import play.api.data.Form
import play.api.i18n.Messages
import views.helpers.ImplicitDateFormatter

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DateOfBenefitCrystallisationEventFormProvider @Inject() extends Mappings with ImplicitDateFormatter {

  def apply()(implicit messages: Messages): Form[LocalDate] = {

    val languageTag = if (messages.lang.code == "cy") "cy" else "en"

    val min                               = LocalDate.of(2015, 4, 6)
    val max                               = LocalDate.of(2023, 4, 5)
    val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

    Form(
      "value" -> localDate(
        invalidKey = "dateOfBenefitCrystallisationEvent.error.invalid",
        allRequiredKey = "dateOfBenefitCrystallisationEvent.error.required.all",
        twoRequiredKey = "dateOfBenefitCrystallisationEvent.error.required.two",
        requiredKey = "dateOfBenefitCrystallisationEvent.error.required"
      )
        .verifying(maxDate(max, "dateOfBenefitCrystallisationEvent.error.max", dateToString(max, languageTag)))
        .verifying(minDate(min, "dateOfBenefitCrystallisationEvent.error.min", dateToString(min, languageTag)))
    )
  }
}
