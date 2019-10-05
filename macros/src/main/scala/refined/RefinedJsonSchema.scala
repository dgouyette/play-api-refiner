package refined

import eu.timepit.refined.boolean._
import eu.timepit.refined.collection.{NonEmpty, _}
import eu.timepit.refined.numeric.Positive
import org.slf4j.LoggerFactory
import play.api.libs.json._
import play.routes.compiler.Route
import shapeless.ops.nat.ToInt

import scala.language.experimental.macros

object JsonSchema {

  private lazy val LOGGER = LoggerFactory.getLogger(JsonSchema.getClass.toGenericString)

  def jsonSchema[T]: JsValue = macro impl[T]

  def impl[T: c.WeakTypeTag](c: scala.reflect.macros.whitebox.Context): c.Expr[JsValue] = {
    import c.universe._
    val r = buildJsonSchema(c)
    implicit val l: c.universe.Liftable[JsObject] = Liftable((in: JsObject) => q"play.api.libs.json.Json.parse(${in.toString()})")
    c.Expr[JsValue](q"""${r}""")
  }

  def getJsonSchema(c: scala.reflect.macros.whitebox.Context)(t : c.universe.Type) = {
    import c.universe._
    val r = buildJsonSchema(c)(c.WeakTypeTag(t))
    implicit val l: c.universe.Liftable[JsObject] = Liftable((in: JsObject) => q"play.api.libs.json.Json.parse(${in.toString()})")
    c.Expr[JsValue](q"""${r}""")
  }


  def openAPI(c: scala.reflect.macros.whitebox.Context)(types : List[(Route,c.universe.Type)]) = {
    import c.universe._
    val json = types.map{t =>
      val method = t._1.verb.value.toLowerCase()
      val path = t._1.path.toString()
      Json.obj(
        s"/$path" ->
          Json.obj(method ->
            Json.obj("requestBody" ->
              Json.obj("content"->
                Json.obj("application/json" ->
                  Json.obj("$ref" ->  s"#/components/schemas/${method}_${path}")).as[JsObject])
            ))

      )}.reduce(_ deepMerge _)
    implicit val l: c.universe.Liftable[JsObject] = Liftable((in: JsObject) => q"play.api.libs.json.Json.parse(${in.toString()})")
    val header = Json.obj("openapi"-> "3.0.0")
    val paths = Json.obj("paths"-> json)
    val componentsValue = types.map{t =>
      val method = t._1.verb.value.toLowerCase()
      val path = t._1.path.toString()
      Json.obj(s"${method}_${path}"-> Json.obj( "type"-> "object", "properties"-> buildJsonSchema(c)(c.WeakTypeTag(t._2))))
    }.reduce(_ deepMerge _)
    val components = Json.obj("components"->  Json.obj("schemas" -> componentsValue))
    val info = Json.obj("info"-> Json.obj(
      "title"-> "API",
      "version"-> "v1"
    ))
    c.Expr[JsValue](q"""${info ++ components ++ header ++ paths}""")
  }


  private def buildJsonSchema[T](c: scala.reflect.macros.whitebox.Context)(implicit tag: c.WeakTypeTag[T]) = {
    import c.universe._

    val minSizeTypeOf = typeOf[MinSize[_]]
    val maxSizeTypeOf = typeOf[MaxSize[_]]
    val sizeTC = typeOf[eu.timepit.refined.collection.Size[_]]
    val andTypeOf = typeOf[And[_,_]]
    val listTypeOf = typeOf[List[_]]

    def subTypeOfListTypeOf[P : TypeTag] = typeOf[List[P]]
    val nonEmptyTypeOf = typeOf[NonEmpty]

    val r = weakTypeOf[T].decls.collect {
      case m: MethodSymbol if m.isCaseAccessor =>

        val (typeSymbol,typeArgs) = m.info match {
          case NullaryMethodType(v) => (v.typeSymbol.asType.toType,v.typeArgs)
        }

        val supportedFormats = List("IPv4", "IPv6", "Uri")

        def sizeT(sizeTyp : Type) : Int = {
          val toIntTree = c.inferImplicitValue(c.typecheck(tq"_root_.shapeless.ops.nat.ToInt[$sizeTyp]", mode = c.TYPEmode).tpe, silent = false)
          val toInt = c.eval(c.Expr(c.untypecheck(toIntTree.duplicate)))
          toInt.asInstanceOf[ToInt[_]].apply()
        }

        def size(p : Type) : Int = p.typeArgs match {
          case List(other) => other.toString().replace("shapeless.nat._", "").toInt // ugly patch  but no simple solution for now
        }

        def values(traitType : Symbol) : List[String] = {
          val children = traitType.asClass.knownDirectSubclasses
          val x = children.map(c => Ident(c.asInstanceOf[scala.reflect.internal.Symbols#Symbol].sourceModule.asInstanceOf[Symbol])).toList
          x.map(_.toString().toLowerCase())
        }


        def extractArgs(typeArgs : Seq[Type]) :JsObject =  {
          typeArgs match {
            case _type :: _predicate :: Nil if _type <:< listTypeOf => Json.obj("type" -> "array")  ++ Json.obj("items"-> extractArgs(List(_predicate)))
            case _refinedType :: _type :: _predicate :: Nil   =>extractArgs(List(_refinedType)) ++   extractArgs(List(_type)) ++ extractArgs(List(_predicate))

            case _type :: Nil if _type =:=  typeOf[String]  => Json.obj("type" -> "string")
            case _type :: Nil if _type =:= typeOf[Int]  => Json.obj("type" -> "integer")
            case _type :: Nil if _type =:= typeOf[BigDecimal] ||  _type =:= typeOf[Double] || _type =:= typeOf[Float]  => Json.obj("type" -> "number")

            case _predicate :: Nil if _predicate =:= typeOf[Positive] => Json.obj("minValue" ->JsNumber(1))


            case _type :: Nil if _type <:< subTypeOfListTypeOf[String] => Json.obj("type" -> "array")  ++ Json.obj("items"-> Json.obj("type" -> "string"))
            case _type :: Nil if _type <:< subTypeOfListTypeOf[Int] => Json.obj("type" -> "array")  ++ Json.obj("items"-> Json.obj("type" -> "integer"))
            case _type :: Nil if _type.typeSymbol.asClass.isSealed  => Json.obj("enum" ->values(_type.typeSymbol),"type" -> "string")

            case _predicate :: Nil if supportedFormats.contains(_predicate.typeSymbol.name.toString())  =>Json.obj("format" -> _predicate.typeSymbol.name.toString().toLowerCase())
            case _predicate :: Nil if _predicate <:< minSizeTypeOf =>  Json.obj("minLength" ->JsNumber(size(_predicate)))
            case _predicate :: Nil if _predicate <:< maxSizeTypeOf =>  Json.obj("maxLength" ->  JsNumber(size(_predicate)))
            case _predicate :: Nil if _predicate <:< sizeTC =>  Json.obj("maxLength" ->  JsNumber(sizeT(_predicate)))

            case _predicate :: Nil if _predicate <:< andTypeOf =>   _predicate.typeArgs.map(p =>extractArgs(List(p))).reduce(_ ++ _)
            case _predicate :: Nil  if _predicate =:= nonEmptyTypeOf =>  Json.obj("minLength"-> 1)
            case other => Json.obj()//Json.obj("type" ->  "other", "value"-> other.map(o => "["+o.toString()+"]").mkString(","), "class"-> other.getClass().toGenericString())
          }
        }
        Json.obj(m.name.decodedName.toString->  extractArgs(List(typeSymbol)  ++ typeArgs))
    }
    r.foldLeft(Json.obj())(_ deepMerge _)

  }


}