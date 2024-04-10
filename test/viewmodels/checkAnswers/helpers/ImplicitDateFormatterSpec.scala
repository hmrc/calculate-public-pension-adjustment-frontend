package utils
import java.time.LocalDate
import base.SpecBase
import views.helpers.ImplicitDateFormatter

class ImplicitDateFormatterSpec extends SpecBase with ImplicitDateFormatter {

  "dateToString" - {

    val date: LocalDate = LocalDate.parse("2020-09-29")

    "correctly format a date when lang is English" in {
      dateToString(date, "en") mustBe "29 September 2020"
    }

    "correctly format a date when lang is Welsh" in {
      dateToString(date, "cy") mustBe "29 Medi 2020"
    }

    "default to English if the language is neither English or Welsh" in {
      dateToString(date, "da") mustBe "29 September 2020"
    }
  }
}
