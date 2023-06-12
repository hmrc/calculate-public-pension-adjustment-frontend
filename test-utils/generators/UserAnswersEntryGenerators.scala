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

package generators

import models._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages.annualallowance.preaaquestions.{ScottishTaxpayerFrom2016Page, WhichYearsScottishTaxpayerPage}
import pages.lifetimeallowance._
import pages.setupquestions.ReportingChangePage
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryProtectionReferenceUserAnswersEntry: Arbitrary[(ProtectionReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ProtectionReferencePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryProtectionTypeUserAnswersEntry: Arbitrary[(ProtectionTypePage.type, JsValue)]   =
    Arbitrary {
      for {
        page  <- arbitrary[ProtectionTypePage.type]
        value <- arbitrary[ProtectionType].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryLtaProtectionOrEnhancementsUserAnswersEntry
    : Arbitrary[(LtaProtectionOrEnhancementsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[LtaProtectionOrEnhancementsPage.type]
        value <- arbitrary[LtaProtectionOrEnhancements].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatNewProtectionTypeEnhancementUserAnswersEntry
    : Arbitrary[(WhatNewProtectionTypeEnhancementPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatNewProtectionTypeEnhancementPage.type]
        value <- arbitrary[WhatNewProtectionTypeEnhancement].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryReferenceNewProtectionTypeEnhancementUserAnswersEntry
    : Arbitrary[(ReferenceNewProtectionTypeEnhancementPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ReferenceNewProtectionTypeEnhancementPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryProtectionTypeEnhancementChangedUserAnswersEntry
    : Arbitrary[(ProtectionTypeEnhancementChangedPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ProtectionTypeEnhancementChangedPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDateOfBenefitCrystallisationEventUserAnswersEntry
    : Arbitrary[(DateOfBenefitCrystallisationEventPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DateOfBenefitCrystallisationEventPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHadBenefitCrystallisationEventUserAnswersEntry
    : Arbitrary[(HadBenefitCrystallisationEventPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HadBenefitCrystallisationEventPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }
  implicit lazy val arbitraryReportingChangeUserAnswersEntry: Arbitrary[(ReportingChangePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ReportingChangePage.type]
        value <- arbitrary[ReportingChange].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhichYearsScottishTaxpayerUserAnswersEntry
    : Arbitrary[(WhichYearsScottishTaxpayerPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhichYearsScottishTaxpayerPage.type]
        value <- arbitrary[WhichYearsScottishTaxpayer].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryScottishTaxpayerFrom2016UserAnswersEntry
    : Arbitrary[(ScottishTaxpayerFrom2016Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ScottishTaxpayerFrom2016Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryChangeInTaxChargeUserAnswersEntry: Arbitrary[(ChangeInTaxChargePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ChangeInTaxChargePage.type]
        value <- arbitrary[ChangeInTaxCharge].map(Json.toJson(_))
      } yield (page, value)
    }
}
