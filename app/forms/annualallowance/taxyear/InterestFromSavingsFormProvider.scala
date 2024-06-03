package forms.annualallowance.taxyear

import forms.mappings.Mappings
import play.api.data.Form

import javax.inject.Inject

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
