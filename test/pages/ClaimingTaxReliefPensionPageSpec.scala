package pages

import pages.behaviours.PageBehaviours

class ClaimingTaxReliefPensionPageSpec extends PageBehaviours {

  "ClaimingTaxReliefPensionPage" - {

    beRetrievable[Boolean](ClaimingTaxReliefPensionPage)

    beSettable[Boolean](ClaimingTaxReliefPensionPage)

    beRemovable[Boolean](ClaimingTaxReliefPensionPage)
  }
}
