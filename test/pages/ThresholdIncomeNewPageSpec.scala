package pages

import models.ThresholdIncomeNew
import pages.behaviours.PageBehaviours

class ThresholdIncomeNewSpec extends PageBehaviours {

  "ThresholdIncomeNewPage" - {

    beRetrievable[ThresholdIncomeNew](ThresholdIncomeNewPage)

    beSettable[ThresholdIncomeNew](ThresholdIncomeNewPage)

    beRemovable[ThresholdIncomeNew](ThresholdIncomeNewPage)
  }
}
