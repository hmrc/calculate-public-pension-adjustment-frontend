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

class BlindPersonsAllowanceAmountFormProviderSpec extends IntFieldBehaviours {

  ".value" - {

    val fieldName = "value"

    val periods = Seq(
      (Period._2016, 2290),
      (Period._2017, 2290),
      (Period._2018, 2320),
      (Period._2019, 2390),
      (Period._2020, 2450),
      (Period._2021, 2500),
      (Period._2022, 2520),
      (Period._2023, 2600)
    )

    periods.foreach { case (period, minimum) =>
      val maximum = minimum * 2

      s"for period $period" - {

        val validDataGenerator = intsInRangeWithCommas(minimum, maximum)

        behave like fieldThatBindsValidData(
          newForm(period),
          fieldName,
          validDataGenerator
        )

        behave like intField(
          newForm(period),
          fieldName,
          nonNumericError = FormError(fieldName, "blindPersonsAllowanceAmount.error.nonNumeric", Seq("")),
          wholeNumberError = FormError(fieldName, "blindPersonsAllowanceAmount.error.wholeNumber", Seq(""))
        )

        behave like intFieldWithMinimum(
          newForm(period),
          fieldName,
          minimum = minimum,
          expectedError = FormError(fieldName, "blindPersonsAllowanceAmount.error.minimum", Seq(minimum, s"$period"))
        )

        behave like intFieldWithMaximum(
          newForm(period),
          fieldName,
          maximum = maximum,
          expectedError = FormError(fieldName, "blindPersonsAllowanceAmount.error.maximum", Seq(maximum, s"$period"))
        )

        behave like mandatoryField(
          newForm(period),
          fieldName,
          requiredError = FormError(fieldName, "blindPersonsAllowanceAmount.error.required", Seq(""))
        )
      }
    }
  }

  def newForm(period: Period): Form[BigInt] = {
    val formProvider = new BlindPersonsAllowanceAmountFormProvider()
    formProvider(period, "")()
  }
}
