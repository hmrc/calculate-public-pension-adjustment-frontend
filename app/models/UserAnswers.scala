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

import pages.Page
import play.api.Logging
import play.api.libs.json._
import queries.{Gettable, Settable}
import uk.gov.hmrc.crypto.Sensitive.SensitiveString
import uk.gov.hmrc.crypto.json.JsonEncryption
import uk.gov.hmrc.crypto.{Decrypter, Encrypter}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant
import java.util.UUID
import scala.util.{Failure, Success, Try}

final case class UserAnswers(
  id: String,
  data: JsObject = Json.obj(),
  uniqueId: String = UUID.randomUUID().toString,
  lastUpdated: Instant = Instant.now,
  authenticated: Boolean = false,
  submissionStarted: Boolean = false
) extends Logging {

  def get[A](page: Gettable[A])(implicit rds: Reads[A]): Option[A] =
    Reads.optionNoError(Reads.at(page.path)).reads(data).getOrElse(None)

  def containsAnswerFor(page: Page) =
    page match {
      case settable: Settable[?] =>
        data.removeObject(settable.path) match {
          case JsSuccess(_, _) => true
          case JsError(_)      =>
            false
        }
      case _                     => false
    }

  def set[A](page: Settable[A], value: A, cleanUp: Boolean = true)(implicit writes: Writes[A]): Try[UserAnswers] = {

    val updatedData = data.setObject(page.path, Json.toJson(value)) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(errors)       =>
        Failure(JsResultException(errors))
    }

    updatedData.flatMap { d =>
      val updatedAnswers = copy(data = d)
      if (cleanUp) { page.cleanup(Some(value), updatedAnswers) }
      else {
        Try(updatedAnswers)
      }
    }
  }

  def removePath(jsPath: JsPath): Try[UserAnswers] = {
    val updatedData: Success[JsObject] = data.removeObject(jsPath) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(_)            =>
        Success(data)
    }

    updatedData.flatMap { (d: JsObject) =>
      Try(copy(data = d))
    }
  }

  def remove[A](settable: Settable[A]): Try[UserAnswers] = {

    val updatedData = data.removeObject(settable.path) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(_)            =>
        Success(data)
    }

    updatedData.flatMap { d =>
      val updatedAnswers = copy(data = d)
      settable.cleanup(None, updatedAnswers)
    }
  }
}

object UserAnswers {

  val reads: Reads[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "_id").read[String] and
        (__ \ "data").read[JsObject] and
        (__ \ "uniqueId").read[String] and
        (__ \ "lastUpdated").read(MongoJavatimeFormats.instantFormat) and
        (__ \ "authenticated").read[Boolean] and
        (__ \ "submissionStarted").read[Boolean]
    )(UserAnswers.apply)
  }

  val writes: OWrites[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "_id").write[String] and
        (__ \ "data").write[JsObject] and
        (__ \ "uniqueId").write[String] and
        (__ \ "lastUpdated").write(MongoJavatimeFormats.instantFormat) and
        (__ \ "authenticated").write[Boolean] and
        (__ \ "submissionStarted").write[Boolean]
    )(o => Tuple.fromProductTyped(o))
  }

  implicit val format: OFormat[UserAnswers] = OFormat(reads, writes)

  def encryptedFormat(implicit crypto: Encrypter & Decrypter): OFormat[UserAnswers] = {

    import play.api.libs.functional.syntax._

    implicit val sensitiveFormat: Format[SensitiveString] =
      JsonEncryption.sensitiveEncrypterDecrypter(SensitiveString.apply)

    val encryptedReads: Reads[UserAnswers] =
      (
        (__ \ "_id").read[String] and
          (__ \ "data").read[SensitiveString] and
          (__ \ "uniqueId").read[String] and
          (__ \ "lastUpdated").read(MongoJavatimeFormats.instantFormat) and
          (__ \ "authenticated").read[Boolean] and
          (__ \ "submissionStarted").read[Boolean]
      )((id, data, uniqueId, lastUpdated, authenticated, submissionStarted) =>
        UserAnswers(
          id,
          Json.parse(data.decryptedValue).as[JsObject],
          uniqueId,
          lastUpdated,
          authenticated,
          submissionStarted
        )
      )

    val encryptedWrites: OWrites[UserAnswers] =
      (
        (__ \ "_id").write[String] and
          (__ \ "data").write[SensitiveString] and
          (__ \ "uniqueId").write[String] and
          (__ \ "lastUpdated").write(MongoJavatimeFormats.instantFormat) and
          (__ \ "authenticated").write[Boolean] and
          (__ \ "submissionStarted").write[Boolean]
      )(ua =>
        (
          ua.id,
          SensitiveString(Json.stringify(ua.data)),
          ua.uniqueId,
          ua.lastUpdated,
          ua.authenticated,
          ua.submissionStarted
        )
      )

    OFormat(encryptedReads orElse reads, encryptedWrites)
  }
}
