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

case object LTAPageGroups {

  def changeInTaxChargePageGroup(): Seq[String] =
    Seq(
      "ltaProtectionOrEnhancements",
      "protectionType",
      "protectionReference",
      "enhancementType",
      "internationalEnhancementReference",
      "pensionCreditReference",
      "protectionTypeEnhancementChanged",
      "whatNewProtectionTypeEnhancement",
      "referenceNewProtectionTypeEnhancement",
      "newEnhancementType",
      "newInternationalEnhancementReference",
      "newPensionCreditReference",
      "lifetimeAllowanceCharge",
      "excessLifetimeAllowancePaid",
      "lumpSumValue",
      "annualPaymentValue",
      "whoPaidLTACharge",
      "userSchemeDetails",
      "schemeNameAndTaxRef",
      "quarterChargePaid",
      "yearChargePaid",
      "newExcessLifetimeAllowancePaid",
      "newLumpSumValue",
      "newAnnualPaymentValue",
      "whoPayingExtraLtaCharge",
      "ltaPensionSchemeDetails"
    )

  def changeInLifetimeAllowancePageGroup(): Seq[String] =
    changeInTaxChargePageGroup :+ "changeInTaxCharge"

  def newExcessLifetimeAllowancePaidPageGroup(): Seq[String] =
    Seq(
      "newExcessLifetimeAllowancePaid",
      "newLumpSumValue",
      "newAnnualPaymentValue",
      "whoPayingExtraLtaCharge",
      "ltaPensionSchemeDetails"
    )

}
