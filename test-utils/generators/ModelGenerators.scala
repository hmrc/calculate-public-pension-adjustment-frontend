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

trait ModelGenerators {

  implicit lazy val arbitraryWhoPaidAACharge: Arbitrary[WhoPaidAACharge] =
    Arbitrary {
      Gen.oneOf(WhoPaidAACharge.values.toSeq)
    }

  implicit lazy val arbitrarySchemeNameAndTaxRefType: Arbitrary[SchemeNameAndTaxRef] =
    Arbitrary {
      Gen.oneOf(SchemeNameAndTaxRef.values.toSeq)
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
