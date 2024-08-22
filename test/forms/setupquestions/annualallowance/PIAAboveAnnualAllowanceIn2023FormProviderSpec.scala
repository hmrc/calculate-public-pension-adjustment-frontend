package forms.setupquestions.annualallowance

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class PIAAboveAnnualAllowanceIn2023FormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "pIAAboveAnnualAllowanceIn2023.error.required"
  val invalidKey  = "error.boolean"

  val form = new PIAAboveAnnualAllowanceIn2023FormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
