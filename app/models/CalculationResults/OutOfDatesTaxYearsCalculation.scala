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

package models.CalculationResults

import models.Period
import play.api.libs.json.{Reads, Writes, __}

case class OutOfDatesTaxYearsCalculation(
  period: Period,
  directCompensation: Double,
  indirectCompensation: Double,
  chargePaidByMember: Double,
  chargePaidBySchemes: Double,
  revisedChargableAmountBeforeTaxRate: Int,
  revisedChargableAmountAfterTaxRate: Double,
  unusedAnnualAllowance: Int,
  taxYearSchemes: List[OutOfDatesTaxYearSchemeCalculation],
  adjustedCompensation: Option[Double]
)

object OutOfDatesTaxYearsCalculation {

  implicit lazy val reads: Reads[OutOfDatesTaxYearsCalculation] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "period").read[Period] and
        (__ \ "directCompensation").read[Double] and
        (__ \ "indirectCompensation").read[Double] and
        (__ \ "chargePaidByMember").read[Double] and
        (__ \ "chargePaidBySchemes").read[Double] and
        (__ \ "revisedChargableAmountBeforeTaxRate").read[Int] and
        (__ \ "revisedChargableAmountAfterTaxRate").read[Double] and
        (__ \ "unusedAnnualAllowance").read[Int] and
        (__ \ "taxYearSchemes").read[List[OutOfDatesTaxYearSchemeCalculation]] and
        (__ \ "adjustedCompensation").readNullable[Double]
    )(OutOfDatesTaxYearsCalculation.apply _)

  }

  implicit lazy val writes: Writes[OutOfDatesTaxYearsCalculation] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "period").write[Period] and
        (__ \ "directCompensation").write[Double] and
        (__ \ "indirectCompensation").write[Double] and
        (__ \ "chargePaidByMember").write[Double] and
        (__ \ "chargePaidBySchemes").write[Double] and
        (__ \ "revisedChargableAmountBeforeTaxRate").write[Int] and
        (__ \ "revisedChargableAmountAfterTaxRate").write[Double] and
        (__ \ "unusedAnnualAllowance").write[Int] and
        (__ \ "taxYearSchemes").write[List[OutOfDatesTaxYearSchemeCalculation]] and
        (__ \ "adjustedCompensation").writeNullable[Double]
    )(a =>
      (
        a.period,
        a.directCompensation,
        a.indirectCompensation,
        a.chargePaidByMember,
        a.chargePaidBySchemes,
        a.revisedChargableAmountBeforeTaxRate,
        a.revisedChargableAmountAfterTaxRate,
        a.unusedAnnualAllowance,
        a.taxYearSchemes,
        a.adjustedCompensation
      )
    )
  }

}
