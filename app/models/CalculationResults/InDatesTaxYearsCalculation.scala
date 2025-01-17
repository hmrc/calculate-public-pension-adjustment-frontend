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

case class InDatesTaxYearsCalculation(
  period: Period,
  memberCredit: Double,
  schemeCredit: Double,
  debit: Double,
  chargePaidByMember: Double,
  chargePaidBySchemes: Double,
  revisedChargableAmountBeforeTaxRate: Int,
  revisedChargableAmountAfterTaxRate: Int,
  unusedAnnualAllowance: Int,
  taxYearSchemes: List[InDatesTaxYearSchemeCalculation],
  totalCompensation: Option[Double]
)

object InDatesTaxYearsCalculation {

  implicit lazy val reads: Reads[InDatesTaxYearsCalculation] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "period").read[Period] and
        (__ \ "memberCredit").read[Double] and
        (__ \ "schemeCredit").read[Double] and
        (__ \ "debit").read[Double] and
        (__ \ "chargePaidByMember").read[Double] and
        (__ \ "chargePaidBySchemes").read[Double] and
        (__ \ "revisedChargableAmountBeforeTaxRate").read[Int] and
        (__ \ "revisedChargableAmountAfterTaxRate").read[Int] and
        (__ \ "unusedAnnualAllowance").read[Int] and
        (__ \ "taxYearSchemes").read[List[InDatesTaxYearSchemeCalculation]] and
        (__ \ "totalCompensation").readNullable[Double]
    )(InDatesTaxYearsCalculation.apply _)
  }

  implicit lazy val writes: Writes[InDatesTaxYearsCalculation] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "period").write[Period] and
        (__ \ "memberCredit").write[Double] and
        (__ \ "schemeCredit").write[Double] and
        (__ \ "debit").write[Double] and
        (__ \ "chargePaidByMember").write[Double] and
        (__ \ "chargePaidBySchemes").write[Double] and
        (__ \ "revisedChargableAmountBeforeTaxRate").write[Int] and
        (__ \ "revisedChargableAmountAfterTaxRate").write[Int] and
        (__ \ "unusedAnnualAllowance").write[Int] and
        (__ \ "taxYearSchemes").write[List[InDatesTaxYearSchemeCalculation]] and
        (__ \ "totalCompensation").writeNullable[Double]
    )(a =>
      (
        a.period,
        a.memberCredit,
        a.schemeCredit,
        a.debit,
        a.chargePaidByMember,
        a.chargePaidBySchemes,
        a.revisedChargableAmountBeforeTaxRate,
        a.revisedChargableAmountAfterTaxRate,
        a.unusedAnnualAllowance,
        a.taxYearSchemes,
        a.totalCompensation
      )
    )
  }

}
