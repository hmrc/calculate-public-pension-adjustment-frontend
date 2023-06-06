package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.libs.json.{JsError, JsString, Json}

class WhoPaidLTAChargeSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "WhoPaidLTACharge" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(WhoPaidLTACharge.values.toSeq)

      forAll(gen) {
        whoPaidLTACharge =>

          JsString(whoPaidLTACharge.toString).validate[WhoPaidLTACharge].asOpt.value mustEqual whoPaidLTACharge
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!WhoPaidLTACharge.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[WhoPaidLTACharge] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(WhoPaidLTACharge.values.toSeq)

      forAll(gen) {
        whoPaidLTACharge =>

          Json.toJson(whoPaidLTACharge) mustEqual JsString(whoPaidLTACharge.toString)
      }
    }
  }
}
