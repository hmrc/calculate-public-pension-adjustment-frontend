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

class PersonalAllowanceFormProvider @Inject() extends Mappings {

  def apply(period: Period)(): Form[BigInt] =
    Form(
      "value" -> bigInt(
        "personalAllowance.error.required",
        "personalAllowance.error.wholeNumber",
        "personalAllowance.error.nonNumeric"
      )
        .verifying(
          maximumValue[BigInt](
            getIndividualLimit(period),
            "personalAllowance.error.outOfRange",
            getIndividualLimit(period).toString()
          )
        )
    )

  private def getIndividualLimit(period: Period): BigInt = period match {
    case Period._2016 => BigInt(10600)
    case Period._2017 => BigInt(11000)
    case Period._2018 => BigInt(11500)
    case Period._2019 => BigInt(11850)
    case Period._2020 => BigInt(12500)
    case Period._2021 => BigInt(12500)
    case Period._2022 => BigInt(12570)
    case Period._2023 => BigInt(12570)
  }
}
