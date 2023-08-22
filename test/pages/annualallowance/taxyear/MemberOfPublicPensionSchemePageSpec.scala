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

import models.{CheckMode, NormalMode, Period}
import pages.annualallowance.preaaquestions.DefinedContributionPensionSchemePage
import pages.behaviours.PageBehaviours
import services.PeriodService

class MemberOfPublicPensionSchemePageSpec extends PageBehaviours {

  "MemberOfPublicPensionSchemePage" - {

    beRetrievable[Boolean](MemberOfPublicPensionSchemePage(Period._2018))

    beSettable[Boolean](MemberOfPublicPensionSchemePage(Period._2018))

    beRemovable[Boolean](MemberOfPublicPensionSchemePage(Period._2018))

    "check mode navigation" - {

      "when changing to not being member of public scheme in period, return to check your answers" in {
        PeriodService.allRemedyPeriods.foreach { period =>
          val userAnswers = emptyUserAnswers.set(MemberOfPublicPensionSchemePage(period), false).get

          val nextPageUrl: String =
            MemberOfPublicPensionSchemePage(period).navigate(CheckMode, userAnswers).url

          checkNavigation(nextPageUrl, s"/check-your-answers-period/$period")
        }
      }

      "when changing to being member of public scheme in period, redirect to member more than one scheme in Normal Mode" in {
        PeriodService.allRemedyPeriods.foreach { period =>
          val userAnswers = emptyUserAnswers.set(MemberOfPublicPensionSchemePage(period), true).get

          val nextPageUrl: String =
            MemberOfPublicPensionSchemePage(period).navigate(CheckMode, userAnswers).url

          checkNavigation(nextPageUrl, s"/member-more-than-one-pension/$period")
        }
      }
    }

    "normal mode navigation" - {

      "irrespective of whether user has dc" - {

        "when the user is a member of a scheme in the period, redirect to member more than one scheme" in {
          PeriodService.allRemedyPeriods.foreach { period =>
            val userAnswers = emptyUserAnswers.set(MemberOfPublicPensionSchemePage(period), true).get

            val nextPageUrl: String =
              MemberOfPublicPensionSchemePage(period).navigate(NormalMode, userAnswers).url

            checkNavigation(nextPageUrl, s"/member-more-than-one-pension/$period")
          }
        }
      }

      "when a user does have dc, in any period" - {

        "but not a member of public scheme redirect to dc details" in {
          PeriodService.allRemedyPeriods.foreach { period =>
            val userAnswers = emptyUserAnswers
              .set(MemberOfPublicPensionSchemePage(period), false)
              .get
              .set(DefinedContributionPensionSchemePage, true)
              .get

            val nextPageUrl: String =
              MemberOfPublicPensionSchemePage(period).navigate(NormalMode, userAnswers).url

            checkNavigation(nextPageUrl, s"/contributed-other-db-dc-scheme/$period")
          }
        }
      }

      "when a user does not have dc " - {

        "and not member of public scheme 2016-pre redirect to check your answers" in {
          val userAnswers = emptyUserAnswers
            .set(MemberOfPublicPensionSchemePage(Period._2016PreAlignment), false)
            .get
            .set(DefinedContributionPensionSchemePage, false)
            .get

          val nextPageUrl: String =
            MemberOfPublicPensionSchemePage(Period._2016PreAlignment).navigate(NormalMode, userAnswers).url

          checkNavigation(nextPageUrl, "/check-your-answers-period/2016-pre")
        }

        "and not member of public scheme 2016-post redirect to total income" in {
          val userAnswers = emptyUserAnswers
            .set(MemberOfPublicPensionSchemePage(Period._2016PostAlignment), false)
            .get
            .set(DefinedContributionPensionSchemePage, false)
            .get

          val nextPageUrl: String =
            MemberOfPublicPensionSchemePage(Period._2016PostAlignment).navigate(NormalMode, userAnswers).url

          checkNavigation(nextPageUrl, "/total-income/2016-post")
        }

        "and not member of public scheme 2017 onwards redirect threshold income" in {
          val userAnswers = emptyUserAnswers
            .set(MemberOfPublicPensionSchemePage(Period._2017), false)
            .get
            .set(DefinedContributionPensionSchemePage, false)
            .get

          val nextPageUrl: String =
            MemberOfPublicPensionSchemePage(Period._2017).navigate(NormalMode, userAnswers).url

          checkNavigation(nextPageUrl, "/threshold-income/2017")
        }
      }
    }
  }
}
