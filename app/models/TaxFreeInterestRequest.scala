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

import play.api.libs.json.{Reads, Writes, __}

case class TaxFreeInterestRequest(
                                   period: Period,
                                   income: BigInt,
                                   scottishTaxYears: List[Period]
                                 )

object TaxFreeInterestRequest {

  implicit lazy val reads: Reads[TaxFreeInterestRequest] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "period").read[Period] and
        (__ \ "income").read[BigInt] and
        (__ \ "scottishTaxYears").read[List[Period]]
      )(TaxFreeInterestRequest.apply _)
  }

  implicit lazy val writes: Writes[TaxFreeInterestRequest] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "period").write[Period] and
        (__ \ "income").write[BigInt] and
        (__ \ "scottishTaxYears").write[List[Period]]
      )(a =>
      (
        a.period,
        a.income,
        a.scottishTaxYears
      )
    )
  }

}