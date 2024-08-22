package pages.setupquestions.annualallowance

import models.{CheckMode, LTAKickOutStatus, NormalMode}
import pages.behaviours.PageBehaviours

class NetIncomeAbove190KIn2023PageSpec extends PageBehaviours {

  "NetIncomeAbove190KIn2023Page" - {

    beRetrievable[Boolean](NetIncomeAbove190KIn2023Page)

    beSettable[Boolean](NetIncomeAbove190KIn2023Page)

    beRemovable[Boolean](NetIncomeAbove190KIn2023Page)
  }

  "normal mode" - {

    "when yes is selected for page" - {

      "when lta kickout status 0 to cya" in {
        val userAnswers = emptyUserAnswers
          .set(NetIncomeAbove190KIn2023Page, true)
          .success
          .value
          .set(LTAKickOutStatus(), 0)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove190KIn2023Page.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when lta kickout status 1 to bce" in {
        val userAnswers = emptyUserAnswers
          .set(NetIncomeAbove190KIn2023Page, true)
          .success
          .value
          .set(LTAKickOutStatus(), 1)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove190KIn2023Page.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/lifetime-allowance/benefit-crystallisation-event")
      }

      "when lta kickout status 2 to cya" in {
        val userAnswers = emptyUserAnswers
          .set(NetIncomeAbove190KIn2023Page, true)
          .success
          .value
          .set(LTAKickOutStatus(), 2)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove190KIn2023Page.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when lta kickout status any other number to recovery" in {
        val userAnswers = emptyUserAnswers
          .set(NetIncomeAbove190KIn2023Page, true)
          .success
          .value
          .set(LTAKickOutStatus(), 3)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove190KIn2023Page.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }

      "when lta kickout status None to cya" in {
        val userAnswers = emptyUserAnswers
          .set(NetIncomeAbove190KIn2023Page, true)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove190KIn2023Page.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }
    }

    // TODO Wire up to part 4 and change test
    "when no is selected for page redirect to recovery " in {
      val userAnswers = emptyUserAnswers
        .set(NetIncomeAbove190KIn2023Page, false)
        .success
        .value

      val nextPageUrl: String = NetIncomeAbove190KIn2023Page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")

    }

    "when nothing is selected for page redirect to recovery " in {
      val nextPageUrl: String = NetIncomeAbove190KIn2023Page.navigate(NormalMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")

    }
  }

  "check mode" - {
    "Redirect to cya" in {
      val nextPageUrl: String = NetIncomeAbove190KIn2023Page.navigate(CheckMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }
  }
}
