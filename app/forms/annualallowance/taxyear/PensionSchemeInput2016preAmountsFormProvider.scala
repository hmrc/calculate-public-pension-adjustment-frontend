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
import models.PensionSchemeInput2016preAmounts
import play.api.data.Form
import play.api.data.Forms.*

import javax.inject.Inject

class PensionSchemeInput2016preAmountsFormProvider @Inject() extends Mappings {

  def apply(dateString: String): Form[PensionSchemeInput2016preAmounts] = Form(
    mapping(
      "revisedPIA" -> bigInt(
        "pensionSchemeInputAmounts.error.revisedPIA.required",
        "pensionSchemeInputAmounts.error.revisedPIA.wholeNumber",
        "pensionSchemeInputAmounts.error.revisedPIA.nonNumeric",
        Seq(dateString)
      )
        .verifying(
          inRangeWithArg[BigInt](
            0,
            BigInt("999999999"),
            "pensionSchemeInputAmounts.error.revisedPIA.length",
            dateString
          )
        )
    )(PensionSchemeInput2016preAmounts.apply)(o => Some(o.revisedPIA))
  )
}
