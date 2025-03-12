/*
 * Copyright 2025 HM Revenue & Customs
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

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.must.Matchers.mustEqual
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.*


class TaxYearSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks {

  "TaxYear reads" - {
    "correctly deserialize TaxYear2011To2015" in {
      val json = Json.obj("period" -> Period._2013, "pIAPreRemedy" -> 1)
      json.validate[TaxYear] `mustEqual` JsSuccess(
        TaxYear2011To2015(1, Period._2013)
      )
    }

    "correctly deserialize TaxYear2016to2023" in {
      val json = Json.obj(
        "pensionInputAmount" -> 1,
        "taxYearSchemes"     -> List(TaxYearScheme("name", "ref", 1, 1, None)),
        "totalIncome"        -> 1,
        "chargePaidByMember" -> 1,
        "period"             -> Period._2017,
        "incomeSubJourney"   -> IncomeSubJourney(
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None
        )
      )

      json.validate[TaxYear] `mustEqual` JsSuccess(
        TaxYear2016To2023.NormalTaxYear(
          1,
          List(TaxYearScheme("name", "ref", 1, 1, None)),
          1,
          1,
          Period._2017,
          IncomeSubJourney(
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None
          ),
          None
        )
      )
    }
  }

  "TaxYear writes" - {
    "must serialise TaxYear2011To2015" in {

      val json = Json.obj("pensionInputAmount" -> 1, "period" -> Period._2013)
      Json.toJson[TaxYear](TaxYear2011To2015(1, Period._2013)) `mustEqual` json
    }

    "must serialise TaxYear2016To2023" in {

      val json = Json.obj(
        "pensionInputAmount" -> 1,
        "taxYearSchemes"     -> List(TaxYearScheme("name", "ref", 1, 1, None)),
        "totalIncome"        -> 1,
        "chargePaidByMember" -> 1,
        "period"             -> Period._2017,
        "incomeSubJourney"   -> IncomeSubJourney(
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None
        )
      )
      Json.toJson[TaxYear](
        TaxYear2016To2023.NormalTaxYear(
          1,
          List(TaxYearScheme("name", "ref", 1, 1, None)),
          1,
          1,
          Period._2017,
          IncomeSubJourney(
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None
          ),
          None
        )
      ) `mustEqual` json
    }
  }
}