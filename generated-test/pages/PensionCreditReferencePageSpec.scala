package pages

import pages.behaviours.PageBehaviours


class PensionCreditReferencePageSpec extends PageBehaviours {

  "PensionCreditReferencePage" - {

    beRetrievable[String](PensionCreditReferencePage)

    beSettable[String](PensionCreditReferencePage)

    beRemovable[String](PensionCreditReferencePage)
  }
}
