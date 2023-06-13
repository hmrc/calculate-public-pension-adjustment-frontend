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

package forms.annualallowance.taxyear

import forms.mappings.Mappings
import models.PensionSchemeInputAmounts
import play.api.data.Form
import play.api.data.Forms._

import javax.inject.Inject

class PensionSchemeInputAmountsFormProvider @Inject() extends Mappings {

  def apply(): Form[PensionSchemeInputAmounts] = Form(
    mapping(
      "originalPIA" -> int("pensionSchemeInputAmounts.error.originalPIA.required")
        .verifying(inRange(0, Int.MaxValue, "pensionSchemeInputAmounts.error.originalPIA.length")),
      "revisedPIA"  -> int("pensionSchemeInputAmounts.error.revisedPIA.required")
        .verifying(inRange(0, Int.MaxValue, "pensionSchemeInputAmounts.error.revisedPIA.length"))
    )(PensionSchemeInputAmounts.apply)(PensionSchemeInputAmounts.unapply)
  )
}
