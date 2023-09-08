package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.libs.json.{JsError, JsString, Json}

class EnhancementTypeSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "EnhancementType" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(EnhancementType.values.toSeq)

      forAll(gen) {
        enhancementType =>

          JsString(enhancementType.toString).validate[EnhancementType].asOpt.value mustEqual enhancementType
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!EnhancementType.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[EnhancementType] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(EnhancementType.values.toSeq)

      forAll(gen) {
        enhancementType =>

          Json.toJson(enhancementType) mustEqual JsString(enhancementType.toString)
      }
    }
  }
}
