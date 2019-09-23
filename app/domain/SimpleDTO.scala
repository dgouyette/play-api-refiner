package domain

import eu.timepit.refined._
import eu.timepit.refined.string._
import eu.timepit.refined.api._
import eu.timepit.refined.numeric._
import eu.timepit.refined.collection._

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import RefinedRuntimeValidator._
sealed trait Color
case object Blue extends Color
case object White extends Color
case object Red extends Color

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