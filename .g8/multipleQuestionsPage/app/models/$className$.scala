package models

import play.api.libs.json.*

case class $className$ ($field1Name$: String, $field2Name$: String)

object $className$ {
  implicit val format = Json.format[$className$]
}
