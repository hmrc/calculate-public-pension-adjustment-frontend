package pages

import models.WhatNewProtectionTypeEnhancement
import pages.behaviours.PageBehaviours

class WhatNewProtectionTypeEnhancementSpec extends PageBehaviours {

  "WhatNewProtectionTypeEnhancementPage" - {

    beRetrievable[WhatNewProtectionTypeEnhancement](WhatNewProtectionTypeEnhancementPage)

    beSettable[WhatNewProtectionTypeEnhancement](WhatNewProtectionTypeEnhancementPage)

    beRemovable[WhatNewProtectionTypeEnhancement](WhatNewProtectionTypeEnhancementPage)
  }
}
