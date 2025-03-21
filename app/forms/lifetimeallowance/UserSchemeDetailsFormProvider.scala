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
import models.UserSchemeDetails
import play.api.data.Form
import play.api.data.Forms.mapping

import javax.inject.Inject

class UserSchemeDetailsFormProvider @Inject() extends Mappings {

  def apply(): Form[UserSchemeDetails] = Form(
    mapping(
      "name"   -> text("userSchemeDetails.name.error.required")
        .verifying(maxLength(100, "userSchemeDetails.name.error.length")),
      "taxRef" -> pstr(
        "userSchemeDetails.taxRef.error.required",
        "userSchemeDetails.taxRef.error.invalid",
        Seq("""(\d\s*){8}[A-Za-z]{2}""")
      )
    )(UserSchemeDetails.apply)(o => Some(Tuple.fromProductTyped(o)))
  )
}
