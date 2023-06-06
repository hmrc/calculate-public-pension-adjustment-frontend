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

package forms.lifetimeallowance

import forms.mappings.Mappings
import javax.inject.Inject
import models.SchemeNameAndTaxRef
import play.api.data.Form
import play.api.data.Forms.mapping

class SchemeNameAndTaxRefFormProvider @Inject() extends Mappings {

  def apply(): Form[SchemeNameAndTaxRef] = Form(
    mapping(
      "name" -> text("schemeNameAndTaxRef.name.error.required")
        .verifying(maxLength(100, "schemeNameAndTaxRef.name.error.length")),
      "taxRef" -> text("schemeNameAndTaxRef.taxRef.error.required")
        .verifying(maxLength(100, "schemeNameAndTaxRef.taxRef.error.length"))
    )(SchemeNameAndTaxRef.apply)(SchemeNameAndTaxRef.unapply)
  )
}
