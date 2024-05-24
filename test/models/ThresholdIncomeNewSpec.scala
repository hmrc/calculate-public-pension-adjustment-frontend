package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.libs.json.{JsError, JsString, Json}

class ThresholdIncomeNewSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "ThresholdIncomeNew" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(ThresholdIncomeNew.values.toSeq)

      forAll(gen) {
        thresholdIncomeNew =>

          JsString(thresholdIncomeNew.toString).validate[ThresholdIncomeNew].asOpt.value mustEqual thresholdIncomeNew
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!ThresholdIncomeNew.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[ThresholdIncomeNew] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(ThresholdIncomeNew.values.toSeq)

      forAll(gen) {
        thresholdIncomeNew =>

          Json.toJson(thresholdIncomeNew) mustEqual JsString(thresholdIncomeNew.toString)
      }
    }
  }
}
