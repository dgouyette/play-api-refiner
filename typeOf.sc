import eu.timepit.refined._
import eu.timepit.refined.collection._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric._
import scala.reflect.runtime.universe._

import shapeless.ops.nat.ToInt
import shapeless.nat._
import shapeless.Nat

def extractSize[T <: Nat](x: String Refined MinSize[T]) = {

}


val zero : String Refined MinSize[_0] = "xxx"
val un : String Refined MinSize[ToInt[_1]] = "xxx"

extractSize(zero)
extractSize(un)