package schema.json

import org.scalatestplus.play._
import eu.timepit.refined.api.RefType
import eu.timepit.refined.boolean._
import eu.timepit.refined.char._
import eu.timepit.refined.collection._
import eu.timepit.refined.generic._
import eu.timepit.refined.string._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric._
import shapeless.tag.@@
import shapeless.syntax.std.tuple._

import reflect.runtime.universe._
import eu.timepit.refined.types.AllTypes
import org.scalatestplus.play._
import refined.schema

import scala.collection.mutable


class StackSpec extends PlaySpec {

  @schema
  case class MuObject(a : String)

  "A Stack" must {
    "pop values in last-in-first-out order" in {   
        
        
      true mustBe true
    }
   
  }
}