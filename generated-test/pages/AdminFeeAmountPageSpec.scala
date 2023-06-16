package pages

import pages.behaviours.PageBehaviours

class AdminFeeAmountPageSpec extends PageBehaviours {

  "AdminFeeAmountPage" - {

    beRetrievable[Int](AdminFeeAmountPage)

    beSettable[Int](AdminFeeAmountPage)

    beRemovable[Int](AdminFeeAmountPage)
  }
}
