package pages

import pages.behaviours.PageBehaviours

class InterestFromSavingsPageSpec extends PageBehaviours {

  "InterestFromSavingsPage" - {

    beRetrievable[Int](InterestFromSavingsPage)

    beSettable[Int](InterestFromSavingsPage)

    beRemovable[Int](InterestFromSavingsPage)
  }
}
