package forms.annualallowance.taxyear

import forms.behaviours.BooleanFieldBehaviours
import models.Period
import play.api.data.FormError

class ClaimingTaxReliefPensionFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "claimingTaxReliefPension.error.required"
  val invalidKey = "error.boolean"

  val period = Period._2019
  val form = new ClaimingTaxReliefPensionFormProvider()(period)

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
