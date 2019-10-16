package domain

import eu.timepit.refined.api._
import eu.timepit.refined.collection._
import eu.timepit.refined.numeric._
import play.api.libs.json._
import org.dgouyette.refined.JsonSchema
import org.dgouyette.refined.json.RefinedRuntimeValidator._


sealed trait Color
case object Blue extends Color
case object White extends Color
case object Red extends Color

case class BasicDTO(nonEmptyString : String Refined NonEmpty)
case class ArrayOfStringDTO(values : List[String])
case class TwoFieldsDTO(positiveInt : Int Refined Positive, arrayOfString : List[String])

object ArrayOfStringDTO {
  implicit val fmt = Json.format[ArrayOfStringDTO]
  implicit val schema = JsonSchema.asJsValue[ArrayOfStringDTO]
}
object TwoFieldsDTO {
  implicit val fmt = Json.format[TwoFieldsDTO]
  implicit val schema = JsonSchema.asJsValue[TwoFieldsDTO]

}
object BasicDTO {
  implicit val fmt = Json.format[BasicDTO]
  implicit val schema = JsonSchema.asJsValue[BasicDTO]
}
case class LoginDTO(email  : String Refined NonEmpty,
                    password : String Refined NonEmpty)
object LoginDTO{
  implicit val fmt = Json.format[LoginDTO]

}

case class SimpleDTO(first : String Refined NonEmpty,
                    second : Int Refined Positive,
                    third : List[Int],
                    quarte : List[String],
                    enumValue : Color,
                    number : BigDecimal)


object SimpleDTO {

    implicit val cfg = JsonConfiguration(
          discriminator = "_type",
    typeNaming = JsonNaming { fullName =>
        fullName.drop(7).toLowerCase
    })

    implicit val whiteFormat = Json.format[White.type]
    implicit val blueFormat = Json.format[Blue.type]
    implicit val redFormat = Json.format[Red.type]


    implicit val colorFormat: OFormat[Color] = Json.format[Color]
    implicit val fmt = Json.format[SimpleDTO]
}