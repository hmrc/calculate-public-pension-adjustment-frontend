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

package pages.annualallowance.taxyear

import models.{NormalMode, PSTR, Period, SchemeIndex}
import pages.behaviours.PageBehaviours

class WhichSchemeSpec extends PageBehaviours {

  "WhichSchemePage" - {

    beRetrievable[String](WhichSchemePage(Period._2018, SchemeIndex(0)))

    beSettable[String](WhichSchemePage(Period._2018, SchemeIndex(0)))

    beRemovable[String](WhichSchemePage(Period._2018, SchemeIndex(0)))
  }

  "normal mode navigation" - {

    "when a scheme ref is specified capture input amounts" in {
      val userAnswers = emptyUserAnswers.set(WhichSchemePage(Period._2018, SchemeIndex(0)), "schemeRef").get

      val nextPageUrl: String =
        WhichSchemePage(Period._2018, SchemeIndex(0)).navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/pension-scheme-input-amounts/2018/0")
    }

    "when a new scheme is indicated capture scheme details" in {
      val userAnswers = emptyUserAnswers.set(WhichSchemePage(Period._2018, SchemeIndex(0)), PSTR.New).get

      val nextPageUrl: String =
        WhichSchemePage(Period._2018, SchemeIndex(0)).navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/pension-scheme-details/2018/0")
    }
  }
}
