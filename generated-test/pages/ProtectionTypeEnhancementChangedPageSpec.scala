package pages

import pages.behaviours.PageBehaviours

class ProtectionTypeEnhancementChangedPageSpec extends PageBehaviours {

  "ProtectionTypeEnhancementChangedPage" - {

    beRetrievable[Boolean](ProtectionTypeEnhancementChangedPage)

    beSettable[Boolean](ProtectionTypeEnhancementChangedPage)

    beRemovable[Boolean](ProtectionTypeEnhancementChangedPage)
  }
}
