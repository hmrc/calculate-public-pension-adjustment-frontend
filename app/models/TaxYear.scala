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

import scala.util.matching.Regex

case class TaxYear(value: Int)

object TaxYear {

  private val pattern: Regex = """(\d{4})-(\d{4})""".r.anchored

  val TaxYear2012: TaxYear = TaxYear(2012)
  val TaxYear2013: TaxYear = TaxYear(2013)
  val TaxYear2014: TaxYear = TaxYear(2014)

  val PreRemedyTaxYears = List(
    TaxYear2012,
    TaxYear2013,
    TaxYear2014
  )

  def isValidTaxYear(fromYear: Int, toYear: Int) =
    PreRemedyTaxYears.contains(TaxYear(fromYear)) && toYear == fromYear + 1

  def fromString(string: String): Option[TaxYear] =
    string match {
      case pattern(fromYear, toYear) =>
        if (isValidTaxYear(fromYear.toInt, toYear.toInt)) {
          Some(TaxYear(fromYear.toInt))
        } else None
      case _                         =>
        None
    }

  implicit def indexPathBindable(implicit intBinder: PathBindable[Int]): PathBindable[TaxYear] =
    new PathBindable[TaxYear] {

      override def bind(key: String, taxYearString: String): Either[String, TaxYear] =
        fromString(taxYearString) match {
          case Some(taxYear) => Right(taxYear)
          case None          => Left("Invalid tax year")
        }

      override def unbind(key: String, taxYear: TaxYear): String =
        intBinder.unbind(key, taxYear.value) + "-" + intBinder.unbind(key, taxYear.value + 1)
    }
}
