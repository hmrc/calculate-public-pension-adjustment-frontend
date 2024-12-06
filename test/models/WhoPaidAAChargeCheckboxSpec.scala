package models

import generators.ModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.libs.json.{JsError, JsString, Json}

class WhoPaidAAChargeCheckboxSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues with ModelGenerators {

  "WhoPaidAAChargeCheckbox" - {

    "must deserialise valid values" in {

      val gen = arbitrary[WhoPaidAAChargeCheckbox]

      forAll(gen) {
        whoPaidAAChargeCheckbox =>

          JsString(whoPaidAAChargeCheckbox.toString).validate[WhoPaidAAChargeCheckbox].asOpt.value mustEqual whoPaidAAChargeCheckbox
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!WhoPaidAAChargeCheckbox.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[WhoPaidAAChargeCheckbox] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = arbitrary[WhoPaidAAChargeCheckbox]

      forAll(gen) {
        whoPaidAAChargeCheckbox =>

          Json.toJson(whoPaidAAChargeCheckbox) mustEqual JsString(whoPaidAAChargeCheckbox.toString)
      }
    }
  }
}
