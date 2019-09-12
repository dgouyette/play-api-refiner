package refined


import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.numeric.Positive
import scala.language.experimental.macros
import play.api.libs.json._
import play.api.libs.functional.syntax._
import eu.timepit.refined.string.IPv4
import eu.timepit.refined.string.IPv6
import eu.timepit.refined.string.Uri
import eu.timepit.refined.collection._
import eu.timepit.refined.boolean._
import shapeless.nat._
import shapeless.Nat
import shapeless.Nats
import shapeless.ops.nat.ToInt
import scala.collection.immutable.Map
import shapeless.syntax.typeable
import eu.timepit.refined.api.Refined
object JsonSchema {


  def jsonSchema[T]: String = macro impl[T]

  def impl[T: c.WeakTypeTag](c: scala.reflect.macros.whitebox.Context): c.Expr[String] = {
    import c.universe._
    val refinedTypeOf = typeOf[Refined[_, _]]
    val minSizeTypeOf = typeOf[MinSize[_]]
    val maxSizeTypeOf = typeOf[MaxSize[_]]
    val andTypeOf = typeOf[And[_,_]]
    val listTypeOf = typeOf[List[_]]
    val nonEmptyTypeOf = typeOf[NonEmpty]




    val r = weakTypeOf[T].decls.collect {
      case m: MethodSymbol if m.isCaseAccessor =>
        val (typeSymbol,typeArgs) = m.info match {
          case NullaryMethodType(v) => {

            (v.typeSymbol.asType.toType,v.typeArgs)
          }
        }


        val supportedFormats = List("IPv4", "IPv6", "Uri")


        def size(p : Type) : Int = p.typeArgs match {          
            case List(other) => other.toString().replace("shapeless.nat._", "").toInt // ugly patch  but no simple solution for now
      }

      def extractArgs(typeArgs : Seq[Type]) :JsObject =  {
        typeArgs match {

          case _type :: Nil if _type =:=  typeOf[String]  => Json.obj("type" -> "string")
          case _type :: Nil if _type =:= typeOf[Int]  => Json.obj("type" -> "int")
          case _refinedType :: _type :: _predicate :: Nil  if _refinedType <:< refinedTypeOf =>  extractArgs(List(_type)) ++ extractArgs(List(_predicate))
          case _predicate :: Nil if _predicate =:= typeOf[Positive] => Json.obj("minValue" ->JsNumber(1))
          
          case _type ::   _ if _type <:< listTypeOf => Json.obj("type" -> "array")

          
          case _predicate :: Nil if supportedFormats.contains(_predicate.typeSymbol.name.toString())  =>Json.obj("format" -> _predicate.typeSymbol.name.toString().toLowerCase())
          case _predicate :: Nil if _predicate <:< minSizeTypeOf =>  Json.obj("minLength" ->JsNumber(size(_predicate)))
          case _predicate :: Nil if _predicate <:< maxSizeTypeOf =>  Json.obj("maxLength" ->  JsNumber(size(_predicate)))
          case _predicate :: Nil if _predicate <:< andTypeOf =>   _predicate.typeArgs.map(p =>extractArgs(List(p))).reduce(_ ++ _)
          case _predicate :: Nil  if _predicate =:= nonEmptyTypeOf =>  Json.obj("minLength"-> 1)
          case other => Json.obj("type" ->  "other", "value"-> other.map(_.toString()).mkString)
        }
      }
        Json.obj(m.name.decodedName.toString->  extractArgs(List(typeSymbol)  ++ typeArgs))
    }
    val json = r.reduce(_ ++ _)
    c.Expr[String](q"""${json.toString()}""")
  }

}