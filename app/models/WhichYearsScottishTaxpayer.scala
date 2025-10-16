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

package models

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.checkbox.*

sealed trait WhichYearsScottishTaxpayer

object WhichYearsScottishTaxpayer extends Enumerable.Implicits {
  case object _2023 extends WithName("2023") with WhichYearsScottishTaxpayer
  case object _2022 extends WithName("2022") with WhichYearsScottishTaxpayer
  case object _2021 extends WithName("2021") with WhichYearsScottishTaxpayer
  case object _2020 extends WithName("2020") with WhichYearsScottishTaxpayer
  case object _2019 extends WithName("2019") with WhichYearsScottishTaxpayer
  case object _2018 extends WithName("2018") with WhichYearsScottishTaxpayer
  case object _2017 extends WithName("2017") with WhichYearsScottishTaxpayer

  val values: Seq[WhichYearsScottishTaxpayer] = Seq(
    _2023,
    _2022,
    _2021,
    _2020,
    _2019,
    _2018,
    _2017
  )

  def checkboxItems(implicit messages: Messages): Seq[CheckboxItem] =
    values.zipWithIndex.map { case (value, index) =>
      CheckboxItemViewModel(
        content = Text(messages(s"whichYearsScottishTaxpayer.${value.toString}")),
        fieldId = "value",
        index = index,
        value = value.toString
      )
    }

  implicit val enumerable: Enumerable[WhichYearsScottishTaxpayer] =
    Enumerable(values.map(v => v.toString -> v)*)
}
