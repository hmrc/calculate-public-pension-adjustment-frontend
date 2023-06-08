package pages

import pages.behaviours.PageBehaviours


class ReferenceNewProtectionTypeEnhancementPageSpec extends PageBehaviours {

  "ReferenceNewProtectionTypeEnhancementPage" - {

    beRetrievable[String](ReferenceNewProtectionTypeEnhancementPage)

    beSettable[String](ReferenceNewProtectionTypeEnhancementPage)

    beRemovable[String](ReferenceNewProtectionTypeEnhancementPage)
  }
}
