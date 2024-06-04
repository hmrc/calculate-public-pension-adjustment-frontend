package pages.annualallowance.taxyear

import models.Period
import pages.behaviours.PageBehaviours

class ClaimingTaxReliefPensionPageSpec extends PageBehaviours {

  val period = Period._2019

  "ClaimingTaxReliefPensionPage" - {

    beRetrievable[Boolean](ClaimingTaxReliefPensionPage(period))

    beSettable[Boolean](ClaimingTaxReliefPensionPage(period))

    beRemovable[Boolean](ClaimingTaxReliefPensionPage(period))
  }
}
