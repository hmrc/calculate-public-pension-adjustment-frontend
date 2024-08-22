package forms.setupquestions.annualallowance

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class NetIncomeAbove190KIn2023FormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "netIncomeAbove190KIn2023.error.required"
  val invalidKey  = "error.boolean"

  val form = new NetIncomeAbove190KIn2023FormProvider()()

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
