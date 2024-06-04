package pages.annualallowance.taxyear

import models.Period
import pages.behaviours.PageBehaviours

class InterestFromSavingsPageSpec extends PageBehaviours {
  val period = Period._2019

  "InterestFromSavingsPage" - {

    beRetrievable[Int](InterestFromSavingsPage(period))

    beSettable[Int](InterestFromSavingsPage(period))

    beRemovable[Int](InterestFromSavingsPage(period))
  }
}
