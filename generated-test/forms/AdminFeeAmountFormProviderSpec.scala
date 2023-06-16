package forms

import forms.behaviours.IntFieldBehaviours
import play.api.data.FormError

class AdminFeeAmountFormProviderSpec extends IntFieldBehaviours {

  val form = new AdminFeeAmountFormProvider()()

  ".value" - {

    val fieldName = "value"

    val minimum = 1
    val maximum = 100000

    val validDataGenerator = intsInRangeWithCommas(minimum, maximum)

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validDataGenerator
    )

    behave like intField(
      form,
      fieldName,
      nonNumericError  = FormError(fieldName, "adminFeeAmount.error.nonNumeric"),
      wholeNumberError = FormError(fieldName, "adminFeeAmount.error.wholeNumber")
    )

    behave like intFieldWithRange(
      form,
      fieldName,
      minimum       = minimum,
      maximum       = maximum,
      expectedError = FormError(fieldName, "adminFeeAmount.error.outOfRange", Seq(minimum, maximum))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, "adminFeeAmount.error.required")
    )
  }
}
