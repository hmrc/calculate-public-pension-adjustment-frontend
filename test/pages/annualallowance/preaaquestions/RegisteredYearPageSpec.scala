package pages.annualallowance.preaaquestions

import models.Period
import pages.behaviours.PageBehaviours

class RegisteredYearPageSpec extends PageBehaviours {

  "RegisteredYearPage" - {

    beRetrievable[Boolean](RegisteredYearPage(Period._2011))

    beSettable[Boolean](RegisteredYearPage(Period._2011))

    beRemovable[Boolean](RegisteredYearPage(Period._2011))
  }
}
