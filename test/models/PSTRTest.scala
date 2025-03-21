/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import base.SpecBase

class PSTRTest extends SpecBase {

  "PSTR" - {

    "must be constructed when year string is valid" in {
      val pstrOption: Option[PSTR] = PSTR.fromString("12345678RL")
      pstrOption `mustBe` Some(PSTR("12345678RL"))
    }

    "must not be constructed when year string is invalid" in {
      val pstrOption: Option[PSTR] = PSTR.fromString("invalidPSTR")
      pstrOption `mustBe` None
    }
  }
}
