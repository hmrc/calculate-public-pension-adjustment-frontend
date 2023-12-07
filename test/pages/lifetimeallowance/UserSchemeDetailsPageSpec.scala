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

package pages.lifetimeallowance

import models.{CheckMode, NormalMode, UserSchemeDetails}
import pages.behaviours.PageBehaviours

class UserSchemeDetailsPageSpec extends PageBehaviours {

  "UserSchemeDetailsPage" - {

    beRetrievable[UserSchemeDetails](UserSchemeDetailsPage)

    beSettable[UserSchemeDetails](UserSchemeDetailsPage)

    beRemovable[UserSchemeDetails](UserSchemeDetailsPage)
  }

  "normal mode navigation" - {

    "when user has entered Scheme details page & hasPreviousCharge doesn't exist" in {

      val userAnswers =
        emptyUserAnswers.set(UserSchemeDetailsPage, models.UserSchemeDetails("Some scheme", "01234567RT")).get

      val nextPageUrl: String = UserSchemeDetailsPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user has entered Scheme details page & hasPreviousCharge is false" in {

      val userAnswers =
        emptyUserAnswers
          .set(LifetimeAllowanceChargePage, false)
          .get
          .set(UserSchemeDetailsPage, models.UserSchemeDetails("Some scheme", "01234567RT"))
          .get

      val nextPageUrl: String = UserSchemeDetailsPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user has entered Scheme details page & hasPreviousCharge is true" in {

      val userAnswers =
        emptyUserAnswers
          .set(LifetimeAllowanceChargePage, true)
          .get
          .set(UserSchemeDetailsPage, models.UserSchemeDetails("Some scheme", "01234567RT"))
          .get

      val nextPageUrl: String = UserSchemeDetailsPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/new-excess-paid")
    }

    "when user hasn't entered scheme details" in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = UserSchemeDetailsPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode navigation" - {

    "when user has entered Scheme details in Check mode & hasPreviousCharge doesn't exist" in {

      val userAnswers =
        emptyUserAnswers.set(UserSchemeDetailsPage, models.UserSchemeDetails("Some scheme", "01234567RT")).get

      val nextPageUrl: String = UserSchemeDetailsPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user has entered Scheme details page & hasPreviousCharge is false" in {

      val userAnswers =
        emptyUserAnswers
          .set(LifetimeAllowanceChargePage, false)
          .get
          .set(UserSchemeDetailsPage, models.UserSchemeDetails("Some scheme", "01234567RT"))
          .get

      val nextPageUrl: String = UserSchemeDetailsPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user has entered Scheme details page & hasPreviousCharge is true & newExcessPaid exists" in {

      val userAnswers =
        emptyUserAnswers
          .set(LifetimeAllowanceChargePage, true)
          .get
          .set(UserSchemeDetailsPage, models.UserSchemeDetails("Some scheme", "01234567RT"))
          .get

      val nextPageUrl: String = UserSchemeDetailsPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-new-excess-paid")
    }

    "when user has entered Scheme details page & hasPreviousCharge is true & newExcessPaid does not exist" in {

      val userAnswers =
        emptyUserAnswers
          .set(LifetimeAllowanceChargePage, true)
          .get
          .set(UserSchemeDetailsPage, models.UserSchemeDetails("Some scheme", "01234567RT"))
          .get
          .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
          .get

      val nextPageUrl: String = UserSchemeDetailsPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user hasn't entered Scheme details in Check mode " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = UserSchemeDetailsPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }
}
