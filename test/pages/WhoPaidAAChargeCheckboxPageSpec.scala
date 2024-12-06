package pages

import models.WhoPaidAAChargeCheckbox
import pages.annualallowance.taxyear.WhoPaidAAChargeCheckboxPage
import pages.behaviours.PageBehaviours

class WhoPaidAAChargeCheckboxPageSpec extends PageBehaviours {

  "WhoPaidAAChargeCheckboxPage" - {

    beRetrievable[Set[WhoPaidAAChargeCheckbox]](WhoPaidAAChargeCheckboxPage)

    beSettable[Set[WhoPaidAAChargeCheckbox]](WhoPaidAAChargeCheckboxPage)

    beRemovable[Set[WhoPaidAAChargeCheckbox]](WhoPaidAAChargeCheckboxPage)
  }
}
