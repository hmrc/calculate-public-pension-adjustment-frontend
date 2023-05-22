/*
 * Copyright 2023 HM Revenue & Customs
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

package forms.annualAllowance.setupQuestions

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class StopPayingPublicPensionFormProvider @Inject() extends Mappings {

  def apply(): Form[LocalDate] = {

    val min                               = LocalDate.of(2015, 4, 6)
    val max                               = LocalDate.of(2022, 4, 5)
    val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

    Form(
      "value" -> localDate(
        invalidKey = "stopPayingPublicPension.error.invalid",
        allRequiredKey = "stopPayingPublicPension.error.required.all",
        twoRequiredKey = "stopPayingPublicPension.error.required.two",
        requiredKey = "stopPayingPublicPension.error.required"
      )
        .verifying(maxDate(max, "stopPayingPublicPension.error.max", max.format(dateTimeFormat)))
        .verifying(minDate(min, "stopPayingPublicPension.error.min", min.format(dateTimeFormat)))
    )
  }
}
