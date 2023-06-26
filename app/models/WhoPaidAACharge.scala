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

sealed trait WhoPaidAACharge

object WhoPaidAACharge extends Enumerable.Implicits {

  case object You extends WithName("you") with WhoPaidAACharge
  case object Scheme extends WithName("scheme") with WhoPaidAACharge
  case object Both extends WithName("both") with WhoPaidAACharge

  val values: Seq[WhoPaidAACharge] = Seq(
    You,
    Scheme,
    Both
  )

  def options(implicit messages: Messages): Seq[RadioItem] = {
    val normalOptions =
      values.zipWithIndex.map { case (value, index) =>
    RadioItem(
      content = Text(messages(s"whoPaidAACharge.${value.toString}")),
      value = Some(value.toString),
      id = Some(s"value_$index")
    )
  }

  val orOption = RadioItem(
    value = None,
    id = Some("divider"),
    disabled = true,
    divider = Some(messages("divider"))
  )
    normalOptions.slice(0, 2) ++ Seq(orOption) ++ normalOptions.slice(2, normalOptions.length)
}

  implicit val enumerable: Enumerable[WhoPaidAACharge] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
