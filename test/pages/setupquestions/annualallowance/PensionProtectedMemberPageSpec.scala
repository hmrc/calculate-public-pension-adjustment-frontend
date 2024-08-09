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

package pages.setupquestions.annualallowance

import models.{CheckMode, NormalMode}
import org.scalacheck.Gen
import org.scalacheck.rng.Seed.random
import pages.behaviours.PageBehaviours
import pages.setupquestions.SavingsStatementPage

import scala.util.Random

class PensionProtectedMemberPageSpec extends PageBehaviours {

  "PensionProtectedMemberPage" - {

    beRetrievable[Boolean](PensionProtectedMemberPage)

    beSettable[Boolean](PensionProtectedMemberPage)

    beRemovable[Boolean](PensionProtectedMemberPage)
  }

  "normal mode" - {

    "must go to AA Kickout when yes and RPSS no" in {

      val userAnswers = emptyUserAnswers
        .set(PensionProtectedMemberPage, true)
        .success
        .value
        .set(SavingsStatementPage, false)
        .success
        .value

      val nextPageUrl: String = PensionProtectedMemberPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/not-impacted-no-RPSS")
    }

    "must go to 22/23 PIA > 40K page when yes and RPSS yes" in {
      // TODO
    }

    "must go to AA charge when no" in {

      val randomBoolean = Random.nextBoolean()
      val userAnswer    = emptyUserAnswers
        .set(PensionProtectedMemberPage, false)
        .success
        .value
        .set(SavingsStatementPage, randomBoolean)
        .success
        .value

      val nextPageUrl: String = PensionProtectedMemberPage.navigate(NormalMode, userAnswer).url

      checkNavigation(nextPageUrl, "/annual-allowance-charge")
    }

    "must go to journey recovery when no answer" in {}
  }

  "check mode" - {

    "when answered must go to CYA" in {

      val userAnswer = emptyUserAnswers
        .set(PensionProtectedMemberPage, false)
        .success
        .value

      val nextPageUrl: String = PensionProtectedMemberPage.navigate(CheckMode, userAnswer).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")

    }

    "when no answer must go to journey recovery" in {}
  }
}
