package pages

import pages.behaviours.PageBehaviours
import pages.lifetimeallowance.SchemeNameAndTaxRefPage


class SchemeNameAndTaxRefPageSpec extends PageBehaviours {

  "SchemeNameAndTaxRefPage" - {

    beRetrievable[String](SchemeNameAndTaxRefPage)

    beSettable[String](SchemeNameAndTaxRefPage)

    beRemovable[String](SchemeNameAndTaxRefPage)
  }
}
