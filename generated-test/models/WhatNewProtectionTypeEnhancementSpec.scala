package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.libs.json.{JsError, JsString, Json}

class WhatNewProtectionTypeEnhancementSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "WhatNewProtectionTypeEnhancement" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(WhatNewProtectionTypeEnhancement.values.toSeq)

      forAll(gen) {
        whatNewProtectionTypeEnhancement =>

          JsString(whatNewProtectionTypeEnhancement.toString).validate[WhatNewProtectionTypeEnhancement].asOpt.value mustEqual whatNewProtectionTypeEnhancement
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!WhatNewProtectionTypeEnhancement.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[WhatNewProtectionTypeEnhancement] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(WhatNewProtectionTypeEnhancement.values.toSeq)

      forAll(gen) {
        whatNewProtectionTypeEnhancement =>

          Json.toJson(whatNewProtectionTypeEnhancement) mustEqual JsString(whatNewProtectionTypeEnhancement.toString)
      }
    }
  }
}
