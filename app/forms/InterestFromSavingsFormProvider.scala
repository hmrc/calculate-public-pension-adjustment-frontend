package forms

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class InterestFromSavingsFormProvider @Inject() extends Mappings {

  def apply(): Form[Int] =
    Form(
      "value" -> int(
        "interestFromSavings.error.required",
        "interestFromSavings.error.wholeNumber",
        "interestFromSavings.error.nonNumeric")
          .verifying(inRange(1, 999999999, "interestFromSavings.error.outOfRange"))
    )
}
