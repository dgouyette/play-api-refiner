package org.dgouyette.refined.json

import eu.timepit.refined._
import eu.timepit.refined.api._
import eu.timepit.refined.auto._
import play.api.libs.json._

object RefinedRuntimeValidator{

    implicit def refinedR[T,P](implicit readsT : Reads[T], v : Validate[T,P]) : Reads[T Refined P] =
      (json: JsValue) => readsT
        .reads(json)
        .flatMap { t: T =>
          refineV[P](t) match {
            case Left(error) => JsError(error)
            case Right(value) => JsSuccess(value)
          }
        }


    implicit def refinedW[T,P](implicit w: Writes[T]) : Writes[T Refined P] = new Writes[T Refined P] {
        def writes(o: Refined[T,P]): JsValue =  w.writes(o)
    }
}