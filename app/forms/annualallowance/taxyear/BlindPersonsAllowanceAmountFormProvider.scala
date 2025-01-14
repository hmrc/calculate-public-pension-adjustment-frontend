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

import javax.inject.Inject

class BlindPersonsAllowanceAmountFormProvider @Inject() extends Mappings {

  def apply(period: Period, startEndDate: String)(): Form[BigInt] =
    Form(
      "value" -> bigInt(
        "blindPersonsAllowanceAmount.error.required",
        "blindPersonsAllowanceAmount.error.wholeNumber",
        "blindPersonsAllowanceAmount.error.nonNumeric",
        Seq(startEndDate)
      )
        .verifying(
          minimumValueTwoArgs[BigInt](
            getIndividualLimit(period),
            "blindPersonsAllowanceAmount.error.minimum",
            Seq(getIndividualLimit(period).toString(), startEndDate)
          ),
          maximumValueTwoArgs[BigInt](
            getIndividualLimit(period) * 2,
            "blindPersonsAllowanceAmount.error.maximum",
            Seq((getIndividualLimit(period) * 2).toString(), startEndDate)
          )
        )
    )

  private def getIndividualLimit(period: Period): BigInt = period match {
    case Period._2016 | Period._2017 => BigInt(2290)
    case Period._2018                => BigInt(2320)
    case Period._2019                => BigInt(2390)
    case Period._2020                => BigInt(2450)
    case Period._2021                => BigInt(2500)
    case Period._2022                => BigInt(2520)
    case Period._2023                => BigInt(2600)
  }
}
