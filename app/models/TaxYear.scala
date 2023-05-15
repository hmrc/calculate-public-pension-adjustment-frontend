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

import play.api.mvc.PathBindable

case class TaxYear(value: Int)

object TaxYear {

  val TaxYear2012: TaxYear = TaxYear(2012)
  val TaxYear2013: TaxYear = TaxYear(2013)
  val TaxYear2014: TaxYear = TaxYear(2014)

  implicit def indexPathBindable(implicit intBinder: PathBindable[Int]): PathBindable[TaxYear] =
    new PathBindable[TaxYear] {

      val PreRemedyTaxYears = List(
        TaxYear2012,
        TaxYear2013,
        TaxYear2014
      )

      def isValidTaxYear(x: Int) = PreRemedyTaxYears.contains(TaxYear(x))

      override def bind(key: String, value: String): Either[String, TaxYear] =
        intBinder.bind(key, value) match {
          case Right(x) if isValidTaxYear(x) => Right(TaxYear(x))
          case _                             => Left("TaxYear binding failed")
        }

      override def unbind(key: String, value: TaxYear): String =
        intBinder.unbind(key, value.value)
    }
}
