package pages

import models.EnhancementType
import pages.behaviours.PageBehaviours

class EnhancementTypeSpec extends PageBehaviours {

  "EnhancementTypePage" - {

    beRetrievable[EnhancementType](EnhancementTypePage)

    beSettable[EnhancementType](EnhancementTypePage)

    beRemovable[EnhancementType](EnhancementTypePage)
  }
}
