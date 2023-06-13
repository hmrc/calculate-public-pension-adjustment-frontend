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

package services

import base.SpecBase
import models.{PSTR, PensionSchemeDetails, Period, SchemeIndex}
import pages.annualallowance.taxyear.PensionSchemeDetailsPage

class SchemeServiceTest extends SpecBase {

  "WhichScheme" - {

    "options should include an option to add a new scheme if there are no previous schemes" in {
      SchemeService.whichScheme(emptyUserAnswers).values must be(Seq(PSTR.New))
    }

    "options should include previous scheme references from a single period and new" in {
      val userAnswers = emptyUserAnswers
        .set(
          PensionSchemeDetailsPage(Period._2016PreAlignment, SchemeIndex(0)),
          PensionSchemeDetails("scheme1", "pstr1")
        )
        .get

      SchemeService.whichScheme(userAnswers).values must be(Seq("pstr1", PSTR.New))
    }

    "options should include multiple scheme references from a single period and new" in {
      val userAnswers = emptyUserAnswers
        .set(
          PensionSchemeDetailsPage(Period._2016PreAlignment, SchemeIndex(0)),
          PensionSchemeDetails("scheme1", "pstr1")
        )
        .get
        .set(
          PensionSchemeDetailsPage(Period._2016PreAlignment, SchemeIndex(1)),
          PensionSchemeDetails("scheme2", "pstr2")
        )
        .get

      SchemeService.whichScheme(userAnswers).values must be(Seq("pstr1", "pstr2", PSTR.New))
    }

    "options should include scheme references from multiple periods and new" in {
      val userAnswers = emptyUserAnswers
        .set(
          PensionSchemeDetailsPage(Period._2016PreAlignment, SchemeIndex(0)),
          PensionSchemeDetails("scheme1", "pstr1")
        )
        .get
        .set(
          PensionSchemeDetailsPage(Period._2016PostAlignment, SchemeIndex(0)),
          PensionSchemeDetails("scheme2", "pstr2")
        )
        .get

      SchemeService.whichScheme(userAnswers).values must be(Seq("pstr1", "pstr2", PSTR.New))
    }
  }

  "maybeAddScheme" - {

    "should not modify user answers if new pstr is specified" in {
      val userAnswers = emptyUserAnswers
        .set(
          PensionSchemeDetailsPage(Period._2016PreAlignment, SchemeIndex(0)),
          PensionSchemeDetails("scheme1", "pstr1")
        )
        .get

      val maybeUpdatedAnswers =
        SchemeService.maybeAddSchemeDetailsToPeriod(userAnswers, PSTR.New, Period._2016PreAlignment, SchemeIndex(1))

      maybeUpdatedAnswers must be(userAnswers)
    }

    "should update user answers if details exist in a previous period and specified reference matches" in {
      val userAnswers = emptyUserAnswers
        .set(
          PensionSchemeDetailsPage(Period._2017, SchemeIndex(1)),
          PensionSchemeDetails("scheme1", "pstr1")
        )
        .get

      val period = Period._2018
      val index  = SchemeIndex(0)

      val maybeUpdatedAnswers = SchemeService.maybeAddSchemeDetailsToPeriod(userAnswers, "pstr1", period, index)

      maybeUpdatedAnswers must not be userAnswers
      val updatedAnswers: Option[PensionSchemeDetails] =
        maybeUpdatedAnswers.get(PensionSchemeDetailsPage(period, index))

      updatedAnswers.get must be(PensionSchemeDetails("scheme1", "pstr1"))
    }

    "should not update user answers if details exist in a previous period and specified reference does not match" in {
      val userAnswers = emptyUserAnswers
        .set(
          PensionSchemeDetailsPage(Period._2017, SchemeIndex(1)),
          PensionSchemeDetails("scheme1", "pstr2")
        )
        .get

      val maybeUpdatedAnswers =
        SchemeService.maybeAddSchemeDetailsToPeriod(userAnswers, "pstr1", Period._2018, SchemeIndex(0))

      maybeUpdatedAnswers must be(userAnswers)
    }

    "should not modify user answers if no previous details exist" in {
      val userAnswers = emptyUserAnswers

      val maybeUpdatedAnswers =
        SchemeService.maybeAddSchemeDetailsToPeriod(userAnswers, "pstr1", Period._2016PreAlignment, SchemeIndex(1))

      maybeUpdatedAnswers must be(userAnswers)
    }
  }
}
