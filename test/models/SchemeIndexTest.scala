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

package models

import base.SpecBase

class SchemeIndexTest extends SpecBase {

  "SchemeIndex" - {

    "must not be constructed when index less than 0" in {
      val schemeIndexOption: Option[SchemeIndex] = SchemeIndex.fromString("-1")
      schemeIndexOption mustBe None
    }

    "must be constructed when index is 0" in {
      val schemeIndexOption: Option[SchemeIndex] = SchemeIndex.fromString("0")
      schemeIndexOption mustBe Some(SchemeIndex(0))
    }

    "must be constructed when index is 4" in {
      val schemeIndexOption: Option[SchemeIndex] = SchemeIndex.fromString("4")
      schemeIndexOption mustBe Some(SchemeIndex(4))
    }

    "must not be constructed when index more than 4" in {
      val schemeIndexOption: Option[SchemeIndex] = SchemeIndex.fromString("5")
      schemeIndexOption mustBe None
    }
  }
}
