package pages

import models.WhoPaidLTACharge
import pages.behaviours.PageBehaviours
import pages.lifetimeallowance.WhoPaidLTAChargePage

class WhoPaidLTAChargeSpec extends PageBehaviours {

  "WhoPaidLTAChargePage" - {

    beRetrievable[WhoPaidLTACharge](WhoPaidLTAChargePage)

    beSettable[WhoPaidLTACharge](WhoPaidLTAChargePage)

    beRemovable[WhoPaidLTACharge](WhoPaidLTAChargePage)
  }
}
