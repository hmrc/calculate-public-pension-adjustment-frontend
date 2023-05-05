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
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.checkbox._

sealed trait WhichYearsScottishTaxpayer

object WhichYearsScottishTaxpayer extends Enumerable.Implicits {

  case object ToTwentyThree extends WithName("toTwentyThree") with WhichYearsScottishTaxpayer
  case object ToTwentyTwo extends WithName("toTwentyTwo") with WhichYearsScottishTaxpayer
  case object ToTwentyOne extends WithName("toTwentyOne") with WhichYearsScottishTaxpayer
  case object ToTwenty extends WithName("toTwenty") with WhichYearsScottishTaxpayer
  case object ToNineteen extends WithName("toNineteen") with WhichYearsScottishTaxpayer
  case object ToEighteen extends WithName("toEighteen") with WhichYearsScottishTaxpayer
  case object ToSeventeen extends WithName("toSeventeen") with WhichYearsScottishTaxpayer

  val values: Seq[WhichYearsScottishTaxpayer] = Seq(
    ToTwentyThree,
    ToTwentyTwo,
    ToTwentyOne,
    ToTwenty,
    ToNineteen,
    ToEighteen,
    ToSeventeen
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
    Enumerable(values.map(v => v.toString -> v): _*)
}
