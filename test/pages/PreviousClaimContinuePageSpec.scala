package pages

import models.PreviousClaimContinue
import pages.behaviours.PageBehaviours

class PreviousClaimContinueSpec extends PageBehaviours {

  "PreviousClaimContinuePage" - {

    beRetrievable[PreviousClaimContinue](PreviousClaimContinuePage)

    beSettable[PreviousClaimContinue](PreviousClaimContinuePage)

    beRemovable[PreviousClaimContinue](PreviousClaimContinuePage)
  }
}
