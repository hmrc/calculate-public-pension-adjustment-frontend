package forms.annualallowance.preaaquestions

import forms.behaviours.BooleanFieldBehaviours
import models.Period
import play.api.data.FormError

class RegisteredYearFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "registeredYear.error.required"
  val invalidKey = "error.boolean"

  val formProvider = new RegisteredYearFormProvider()
  val form = formProvider(Period._2011)


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
