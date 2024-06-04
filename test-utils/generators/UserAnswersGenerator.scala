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

package generators

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import pages.annualallowance.preaaquestions.{ScottishTaxpayerFrom2016Page, WhichYearsScottishTaxpayerPage}
import pages.annualallowance.taxyear._
import pages.lifetimeallowance._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersGenerator extends TryValues {
  self: Generators =>

  val generators: Seq[Gen[(QuestionPage[_], JsValue)]] =
    arbitrary[(UserSchemeDetailsPage.type, JsValue)] ::
      arbitrary[(DefinedContribution2016PreFlexiAmountPage.type, JsValue)] ::
      arbitrary[(DefinedContribution2016PostFlexiAmountPage.type, JsValue)] ::
      arbitrary[(DefinedBenefit2016PostAmountPage.type, JsValue)] ::
      arbitrary[(DefinedContribution2016PreAmountPage.type, JsValue)] ::
      arbitrary[(DefinedContribution2016PostAmountPage.type, JsValue)] ::
      arbitrary[(DefinedBenefit2016PreAmountPage.type, JsValue)] ::
      arbitrary[(UserSchemeDetailsPage.type, JsValue)] ::
      arbitrary[(NewExcessLifetimeAllowancePaidPage.type, JsValue)] ::
      arbitrary[(MultipleBenefitCrystallisationEventPage.type, JsValue)] ::
      arbitrary[(NewLumpSumValuePage.type, JsValue)] ::
      arbitrary[(NewAnnualPaymentValuePage.type, JsValue)] ::
      arbitrary[(YearChargePaidPage.type, JsValue)] ::
      arbitrary[(QuarterChargePaidPage.type, JsValue)] ::
      arbitrary[(PensionCreditReferencePage.type, JsValue)] ::
      arbitrary[(InternationalEnhancementReferencePage.type, JsValue)] ::
      arbitrary[(EnhancementTypePage.type, JsValue)] ::
      arbitrary[(WhoPayingExtraLtaChargePage.type, JsValue)] ::
      arbitrary[(LumpSumValuePage.type, JsValue)] ::
      arbitrary[(AnnualPaymentValuePage.type, JsValue)] ::
      arbitrary[(NewPensionCreditReferencePage.type, JsValue)] ::
      arbitrary[(NewInternationalEnhancementReferencePage.type, JsValue)] ::
      arbitrary[(NewEnhancementTypePage.type, JsValue)] ::
      arbitrary[(WhoPayingExtraLtaChargePage.type, JsValue)] ::
      arbitrary[(ProtectionReferencePage.type, JsValue)] ::
      arbitrary[(ProtectionTypePage.type, JsValue)] ::
      arbitrary[(LtaProtectionOrEnhancementsPage.type, JsValue)] ::
      arbitrary[(WhatNewProtectionTypeEnhancementPage.type, JsValue)] ::
      arbitrary[(ReferenceNewProtectionTypeEnhancementPage.type, JsValue)] ::
      arbitrary[(ProtectionEnhancedChangedPage.type, JsValue)] ::
      arbitrary[(DateOfBenefitCrystallisationEventPage.type, JsValue)] ::
      arbitrary[(DateOfBenefitCrystallisationEventPage.type, JsValue)] ::
      arbitrary[(HadBenefitCrystallisationEventPage.type, JsValue)] ::
      arbitrary[(WhichYearsScottishTaxpayerPage.type, JsValue)] ::
      arbitrary[(ScottishTaxpayerFrom2016Page.type, JsValue)] ::
      arbitrary[(ChangeInTaxChargePage.type, JsValue)] ::
      arbitrary[(ExcessLifetimeAllowancePaidPage.type, JsValue)] ::
      arbitrary[(LifetimeAllowanceChargePage.type, JsValue)] ::
      arbitrary[(SchemeNameAndTaxRefPage.type, JsValue)] ::
      arbitrary[(WhoPaidLTAChargePage.type, JsValue)] ::
      Nil

  implicit lazy val arbitraryUserData: Arbitrary[UserAnswers] = {

    import models._

    Arbitrary {
      for {
        id   <- nonEmptyString
        data <- generators match {
                  case Nil => Gen.const(Map[QuestionPage[_], JsValue]())
                  case _   => Gen.mapOf(oneOf(generators))
                }
      } yield UserAnswers(
        id = id,
        data = data.foldLeft(Json.obj()) { case (obj, (path, value)) =>
          obj.setObject(path.path, value).get
        }
      )
    }
  }
}
