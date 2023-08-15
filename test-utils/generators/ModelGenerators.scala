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
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary

trait ModelGenerators {

  // scala fmt ignore

  implicit lazy val arbitraryPeriod: Arbitrary[Period] =
    Arbitrary {
      Gen.oneOf(
        Set(
          Period._2013,
          Period._2014,
          Period._2015,
          Period._2016PreAlignment,
          Period._2016PostAlignment,
          Period._2017,
          Period._2019,
          Period._2020,
          Period._2021,
          Period._2022,
          Period._2023
        )
      )
    }

  implicit lazy val arbitraryContributedToDuringRemedyPeriod: Arbitrary[ContributedToDuringRemedyPeriod] =
    Arbitrary {
      Gen.oneOf(ContributedToDuringRemedyPeriod.values)
    }

  implicit lazy val arbitraryLtaPensionSchemeDetails: Arbitrary[LtaPensionSchemeDetails] =
    Arbitrary {
      for {
        name   <- arbitrary[String]
        taxRef <- taxRef
      } yield LtaPensionSchemeDetails(name, taxRef)
    }

  implicit lazy val arbitraryWhoPayingExtraLtaCharge: Arbitrary[WhoPayingExtraLtaCharge] =
    Arbitrary {
      Gen.oneOf(WhoPayingExtraLtaCharge.values.toSeq)
    }

  implicit lazy val arbitraryWhoPaidAACharge: Arbitrary[WhoPaidAACharge] =
    Arbitrary {
      Gen.oneOf(WhoPaidAACharge.values.toSeq)
    }

  def taxRef: Gen[String] =
    for {
      digit  <- Gen.listOfN(8, Gen.numChar).map(_.mkString)
      letter <- Gen.listOfN(2, Gen.alphaUpperChar).map(_.mkString)

    } yield s"$digit$letter"

  implicit lazy val arbitrarySchemeNameAndTaxRefType: Arbitrary[SchemeNameAndTaxRef] =
    Arbitrary {
      for {
        name   <- arbitrary[String]
        taxRef <- taxRef
      } yield SchemeNameAndTaxRef(name, taxRef)
    }

  implicit lazy val arbitraryProtectionType: Arbitrary[ProtectionType] =
    Arbitrary {
      Gen.oneOf(ProtectionType.values.toSeq)
    }

  implicit lazy val arbitraryWhatNewProtectionTypeEnhancement: Arbitrary[WhatNewProtectionTypeEnhancement] =
    Arbitrary {
      Gen.oneOf(WhatNewProtectionTypeEnhancement.values.toSeq)
    }

  implicit lazy val arbitraryLtaProtectionOrEnhancements: Arbitrary[LtaProtectionOrEnhancements] =
    Arbitrary {
      Gen.oneOf(LtaProtectionOrEnhancements.values.toSeq)
    }

  implicit lazy val arbitraryWhoPaidLTACharge: Arbitrary[WhoPaidLTACharge] =
    Arbitrary {
      Gen.oneOf(WhoPaidLTACharge.values.toSeq)
    }

  implicit lazy val arbitraryExcessLifetimeAllowancePaid: Arbitrary[ExcessLifetimeAllowancePaid] =
    Arbitrary {
      Gen.oneOf(ExcessLifetimeAllowancePaid.values.toSeq)
    }
  implicit lazy val arbitraryReportingChange: Arbitrary[ReportingChange]                         =
    Arbitrary {
      Gen.oneOf(ReportingChange.values)
    }

  implicit lazy val arbitraryWhichYearsScottishTaxpayer: Arbitrary[WhichYearsScottishTaxpayer] =
    Arbitrary {
      Gen.oneOf(WhichYearsScottishTaxpayer.values)
    }

  implicit lazy val arbitraryChangeInTaxCharge: Arbitrary[ChangeInTaxCharge] =
    Arbitrary {
      Gen.oneOf(ChangeInTaxCharge.values.toSeq)
    }
}
