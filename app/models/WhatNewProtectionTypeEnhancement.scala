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

package models

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait WhatNewProtectionTypeEnhancement

object WhatNewProtectionTypeEnhancement extends Enumerable.Implicits {

  case object EnhancedProtection extends WithName(s"option1") with WhatNewProtectionTypeEnhancement
  case object PrimaryProtection extends WithName(s"option2") with WhatNewProtectionTypeEnhancement
  case object FixedProtection extends WithName(s"option3") with WhatNewProtectionTypeEnhancement
  case object FixedProtection2014 extends WithName(s"option4") with WhatNewProtectionTypeEnhancement
  case object FixedProtection2016 extends WithName(s"option5") with WhatNewProtectionTypeEnhancement
  case object IndividualProtection2014 extends WithName(s"option6") with WhatNewProtectionTypeEnhancement
  case object IndividualProtection2016 extends WithName(s"option7") with WhatNewProtectionTypeEnhancement
  case object InternationalEnhancement extends WithName(s"option8") with WhatNewProtectionTypeEnhancement
  case object PensionCredit extends WithName(s"option9") with WhatNewProtectionTypeEnhancement

  val values: Seq[WhatNewProtectionTypeEnhancement] = Seq(
    EnhancedProtection, PrimaryProtection, FixedProtection,
    FixedProtection2014, FixedProtection2016, IndividualProtection2014,
    IndividualProtection2016, InternationalEnhancement, PensionCredit
  )

  def options(implicit messages: Messages): Seq[RadioItem] = values.zipWithIndex.map {
    case (value, index) =>
      RadioItem(
        content = Text(messages(s"whatNewProtectionTypeEnhancement.${value.toString}")),
        value   = Some(value.toString),
        id      = Some(s"value_$index")
      )
  }

  implicit val enumerable: Enumerable[WhatNewProtectionTypeEnhancement] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
