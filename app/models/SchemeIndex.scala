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

import play.api.mvc.PathBindable

case class SchemeIndex(value: Int) {
  override def toString: String = value.toString
}

object SchemeIndex {

  def fromString(string: String): Option[SchemeIndex] =
    string.toIntOption match {
      case Some(i) if i >= 0 && i <= 4 => Some(SchemeIndex(i))
      case _                           => None
    }

  implicit def indexPathBindable(implicit intBinder: PathBindable[Int]): PathBindable[SchemeIndex] =
    new PathBindable[SchemeIndex] {

      override def bind(key: String, indexString: String): Either[String, SchemeIndex] =
        fromString(indexString) match {
          case Some(schemeIndex: SchemeIndex) => Right(schemeIndex)
          case None                           => Left("Invalid scheme index")
        }

      override def unbind(key: String, index: SchemeIndex): String =
        intBinder.unbind(key, index.value)
    }
}
