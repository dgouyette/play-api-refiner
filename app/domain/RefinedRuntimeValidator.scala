package domain

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import eu.timepit.refined._
import eu.timepit.refined.api._
import eu.timepit.refined.auto._
import eu.timepit.refined.api.RefType
import eu.timepit.refined.boolean._
import eu.timepit.refined.char._
import eu.timepit.refined.collection._
import eu.timepit.refined.numeric._
import eu.timepit.refined.generic._
import eu.timepit.refined.string._
import eu.timepit.refined.types.numeric.PosInt

object RefinedRuntimeValidator{

    implicit def refinedR[T,P](implicit readsT : Reads[T], v : Validate[T,P]) : Reads[T Refined P] = new Reads[T Refined P] {
        def reads(json: JsValue) = {
            readsT
            .reads(json)
            .flatMap{ t : T => 
                refineV[P](t) match {
                    case Left(error) => JsError(error)
                    case  Right(value) => JsSuccess(value)
                }
            }   
        }         
    }

    implicit def refinedW[T,P](implicit w: Writes[T]) : Writes[T Refined P] = new Writes[T Refined P] {
        def writes(o: Refined[T,P]): JsValue =  w.writes(o)
    }  
}