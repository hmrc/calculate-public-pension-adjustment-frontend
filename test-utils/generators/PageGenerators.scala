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

import org.scalacheck.Arbitrary
import pages._
import pages.annualallowance.preaaquestions.{ScottishTaxpayerFrom2016Page, WhichYearsScottishTaxpayerPage}
import pages.setupquestions.ReportingChangePage

trait PageGenerators {
  implicit lazy val arbitraryReportingChangePage: Arbitrary[ReportingChangePage.type] =
    Arbitrary(ReportingChangePage)

  implicit lazy val arbitraryWhichYearsScottishTaxpayerPage: Arbitrary[WhichYearsScottishTaxpayerPage.type] =
    Arbitrary(WhichYearsScottishTaxpayerPage)

  implicit lazy val arbitraryScottishTaxpayerFrom2016Page: Arbitrary[ScottishTaxpayerFrom2016Page.type] =
    Arbitrary(ScottishTaxpayerFrom2016Page)

}
