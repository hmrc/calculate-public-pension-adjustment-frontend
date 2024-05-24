package forms

import forms.behaviours.OptionFieldBehaviours
import models.ThresholdIncomeNew
import play.api.data.FormError

class ThresholdIncomeNewFormProviderSpec extends OptionFieldBehaviours {

  val form = new ThresholdIncomeNewFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "thresholdIncomeNew.error.required"

    behave like optionsField[ThresholdIncomeNew](
      form,
      fieldName,
      validValues  = ThresholdIncomeNew.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
