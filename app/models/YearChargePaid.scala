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
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait YearChargePaid

object YearChargePaid extends Enumerable.Implicits {

  case object _2022To2023 extends WithName("2022To2023") with YearChargePaid

  case object _2021To2022 extends WithName("2021To2022") with YearChargePaid

  case object _2020To2021 extends WithName("2020To2021") with YearChargePaid

  case object _2019To2020 extends WithName("2019To2020") with YearChargePaid

  case object _2018To2019 extends WithName("2018To2019") with YearChargePaid

  case object _2017To2018 extends WithName("2017To2018") with YearChargePaid

  case object _2016To2017 extends WithName("2016To2017") with YearChargePaid

  case object _2015To2016 extends WithName("2015To2016") with YearChargePaid

  val values: Seq[YearChargePaid] = Seq(
    _2022To2023,
    _2021To2022,
    _2020To2021,
    _2019To2020,
    _2018To2019,
    _2017To2018,
    _2016To2017,
    _2015To2016
  )

  def options(implicit messages: Messages): Seq[RadioItem] = values.zipWithIndex.map { case (value, index) =>
    RadioItem(
      content = Text(messages(s"yearChargePaid.${value.toString}")),
      value = Some(value.toString),
      id = Some(s"value_$index")
    )
  }

  implicit val enumerable: Enumerable[YearChargePaid] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
