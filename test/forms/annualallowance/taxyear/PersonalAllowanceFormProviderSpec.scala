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

import forms.behaviours.IntFieldBehaviours
import models.Period
import play.api.data.{Form, FormError}

class PersonalAllowanceFormProviderSpec extends IntFieldBehaviours {

  ".value" - {

    val fieldName = "value"

    val periods = Seq(
      (Period._2016, 10600),
      (Period._2017, 11000),
      (Period._2018, 11500),
      (Period._2019, 11850),
      (Period._2020, 12500),
      (Period._2021, 12500),
      (Period._2022, 12570),
      (Period._2023, 12570)
    )

    periods.foreach { case (period, maximum) =>
      s"for period $period" - {

        val validDataGenerator = intsInRangeWithCommas(0, maximum)

        behave like fieldThatBindsValidData(
          newForm(period),
          fieldName,
          validDataGenerator
        )

        behave like intField(
          newForm(period),
          fieldName,
          nonNumericError = FormError(fieldName, "personalAllowance.error.nonNumeric"),
          wholeNumberError = FormError(fieldName, "personalAllowance.error.wholeNumber")
        )

        behave like intFieldWithMaximum(
          newForm(period),
          fieldName,
          maximum = maximum,
          expectedError = FormError(fieldName, "personalAllowance.error.outOfRange", Seq(maximum, s"$period"))
        )

        behave like mandatoryField(
          newForm(period),
          fieldName,
          requiredError = FormError(fieldName, "personalAllowance.error.required")
        )
      }
    }
  }

  def newForm(period: Period): Form[BigInt] = {
    val formProvider = new PersonalAllowanceFormProvider()
    formProvider(period)()
  }
}
