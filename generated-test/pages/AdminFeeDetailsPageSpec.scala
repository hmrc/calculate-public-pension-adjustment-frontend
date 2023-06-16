package pages

import pages.behaviours.PageBehaviours


class AdminFeeDetailsPageSpec extends PageBehaviours {

  "AdminFeeDetailsPage" - {

    beRetrievable[String](AdminFeeDetailsPage)

    beSettable[String](AdminFeeDetailsPage)

    beRemovable[String](AdminFeeDetailsPage)
  }
}
