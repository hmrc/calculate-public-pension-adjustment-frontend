package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class ClaimingTaxReliefPensionFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "claimingTaxReliefPension.error.required"
  val invalidKey = "error.boolean"

  val form = new ClaimingTaxReliefPensionFormProvider()()

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
