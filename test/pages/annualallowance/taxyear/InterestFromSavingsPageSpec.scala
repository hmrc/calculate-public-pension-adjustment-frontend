package pages.annualallowance.taxyear

import models.Period
import pages.behaviours.PageBehaviours

class InterestFromSavingsPageSpec extends PageBehaviours {

  val period = Period._2019

  "InterestFromSavingsPage" - {

    beRetrievable[BigInt](InterestFromSavingsPage(period))

    beSettable[BigInt](InterestFromSavingsPage(period))

    beRemovable[BigInt](InterestFromSavingsPage(period))
  }
}
