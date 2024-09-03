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

package utils

object ToCamelCase {

  def toCamelCase(str: String, splitterChar: Char): String =
    str.split(splitterChar).map(_.toLowerCase.capitalize).mkString("").uncapitalize

  implicit class StringOps(val str: String) extends AnyVal {
    def uncapitalize: String =
      if (str.isEmpty) str
      else str.charAt(0).toString.toLowerCase + str.substring(1)
  }
}
