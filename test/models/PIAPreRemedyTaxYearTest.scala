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

import base.SpecBase

class PIAPreRemedyTaxYearTest extends SpecBase {

  "TaxYear" - {

    "must be constructed when year string is valid" in {
      val taxYearOption: Option[PIAPreRemedyTaxYear] = PIAPreRemedyTaxYear.fromString("2014-2015")
      taxYearOption mustBe Some(PIAPreRemedyTaxYear(2014))
    }

    "must not be constructed when year string is invalid" in {
      val taxYearOption: Option[PIAPreRemedyTaxYear] = PIAPreRemedyTaxYear.fromString("a-b")
      taxYearOption mustBe None
    }

    "must not be constructed when year string is not valid range" in {
      val taxYearOption: Option[PIAPreRemedyTaxYear] = PIAPreRemedyTaxYear.fromString("2014-2016")
      taxYearOption mustBe None
    }

    "must not be constructed when is not a valid pre-remedy year" in {
      val taxYearOption: Option[PIAPreRemedyTaxYear] = PIAPreRemedyTaxYear.fromString("2011-2012")
      taxYearOption mustBe None
    }
  }
}
