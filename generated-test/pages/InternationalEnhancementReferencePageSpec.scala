package pages

import pages.behaviours.PageBehaviours


class InternationalEnhancementReferencePageSpec extends PageBehaviours {

  "InternationalEnhancementReferencePage" - {

    beRetrievable[String](InternationalEnhancementReferencePage)

    beSettable[String](InternationalEnhancementReferencePage)

    beRemovable[String](InternationalEnhancementReferencePage)
  }
}
