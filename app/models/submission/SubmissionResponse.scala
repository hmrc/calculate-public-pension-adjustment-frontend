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

package models.submission

import play.api.libs.json.{Json, OFormat, OWrites, Reads, JsObject, JsError}

sealed trait SubmissionResponse extends Product with Serializable

final case class Success(uniqueId: String) extends SubmissionResponse

object Success {
  implicit lazy val format: OFormat[Success] = Json.format[Success]
}

final case class Failure(errors: Seq[String]) extends SubmissionResponse {
  implicit lazy val format: OFormat[Failure] = Json.format[Failure]
}

object Failure {

  implicit lazy val format: OFormat[Failure] = Json.format[Failure]
}

object SubmissionResponse {
  implicit lazy val writes: OWrites[SubmissionResponse] = OWrites {
    case success: Success => Json.toJsObject(success)
    case failure: Failure => Json.toJsObject(failure)
  }

  implicit lazy val reads: Reads[SubmissionResponse] = Reads { jsValue =>
    jsValue.asOpt[JsObject] match {
      case Some(obj) if (obj \ "uniqueId").isDefined =>
        Success.format.reads(jsValue)
      case Some(obj) if (obj \ "errors").isDefined =>
        Failure.format.reads(jsValue)
      case _ => JsError("Invalid SubmissionResponse")
    }
  }

  implicit lazy val format: OFormat[SubmissionResponse] = OFormat(reads, writes)
}
