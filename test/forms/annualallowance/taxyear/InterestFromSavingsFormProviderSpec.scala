package forms.annualallowance.taxyear

import forms.behaviours.IntFieldBehaviours
import models.Period
import play.api.data.FormError

class InterestFromSavingsFormProviderSpec extends IntFieldBehaviours {

  val period = Period._2019
  val form = new InterestFromSavingsFormProvider()(period)

  ".value" - {

    val fieldName = "value"

    val minimum = 1
    val maximum = 999999999

    val validDataGenerator = intsInRangeWithCommas(minimum, maximum)

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validDataGenerator
    )

    behave like intField(
      form,
      fieldName,
      nonNumericError  = FormError(fieldName, "interestFromSavings.error.nonNumeric"),
      wholeNumberError = FormError(fieldName, "interestFromSavings.error.wholeNumber")
    )

    behave like intFieldWithRange(
      form,
      fieldName,
      minimum       = minimum,
      maximum       = maximum,
      expectedError = FormError(fieldName, "interestFromSavings.error.outOfRange", Seq(minimum, maximum))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, "interestFromSavings.error.required")
    )
  }
}
