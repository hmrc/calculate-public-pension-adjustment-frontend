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

package utils

import utils.CurrencyFormatter.formatNumberString

trait CurrencyFormatter {
  def currencyFormat(amt: BigInt): String = f"&pound;$amt"
  def currencyFormat(amt: Int): String    = f"&pound;$amt"
  def currencyFormat(string: String): String    = formatNumberString(string)
}

object CurrencyFormatter extends CurrencyFormatter {

  def formatNumberString(input: String): String = {
    if (input.forall(_.isDigit)) {
      val formattedString = input.reverse
        .grouped(3)
        .mkString(",")
        .reverse
      "£" + formattedString
    } else {
      input
    }
  }

}
