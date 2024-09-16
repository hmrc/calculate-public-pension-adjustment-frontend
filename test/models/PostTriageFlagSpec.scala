package models

import base.SpecBase
import org.scalatest.freespec.AnyFreeSpec

class PostTriageFlagSpec extends AnyFreeSpec with SpecBase {

  "setStatusTrue" - {

    "should save status" in {

      val userAnswers = emptyUserAnswers

      val updatedUserAnswers = PostTriageFlag.setStatusTrue(userAnswers)

      updatedUserAnswers.get(PostTriageFlag) mustBe Some(true)
    }
  }
}
