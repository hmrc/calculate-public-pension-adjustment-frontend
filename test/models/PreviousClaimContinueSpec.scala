package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.libs.json.{JsError, JsString, Json}

class PreviousClaimContinueSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "PreviousClaimContinue" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(PreviousClaimContinue.values.toSeq)

      forAll(gen) {
        previousClaimContinue =>

          JsString(previousClaimContinue.toString).validate[PreviousClaimContinue].asOpt.value mustEqual previousClaimContinue
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!PreviousClaimContinue.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[PreviousClaimContinue] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(PreviousClaimContinue.values.toSeq)

      forAll(gen) {
        previousClaimContinue =>

          Json.toJson(previousClaimContinue) mustEqual JsString(previousClaimContinue.toString)
      }
    }
  }
}
