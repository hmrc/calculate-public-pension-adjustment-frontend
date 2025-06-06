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

import play.api.libs.json._
import scala.language.implicitConversions

trait TaxYear {
  def period: Period
}

object TaxYear {

  implicit lazy val reads: Reads[TaxYear] = {

    implicit class ReadsWithContravariantOr[A](a: Reads[A]) {

      def or[B >: A](b: Reads[B]): Reads[B] =
        a.map[B](identity).orElse(b)
    }

    implicit def convertToSupertype[A, B >: A](a: Reads[A]): Reads[B] =
      a.map(identity)

    TaxYear2011To2015.reads or
      TaxYear2016To2023.reads

  }

  implicit lazy val writes: Writes[TaxYear] = Writes {
    case year: TaxYear2011To2015 => Json.toJson(year)(TaxYear2011To2015.writes)
    case year: TaxYear2016To2023 => Json.toJson(year)(TaxYear2016To2023.writes)
    case _                       => throw new Exception("Tax year period is invalid")
  }
}
