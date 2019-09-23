package refined
import play.api.mvc._

import scala.language.experimental.macros

object RouteParser {

  def bodyType(path : String): Any = macro impl

  def impl(c: scala.reflect.macros.whitebox.Context)(path: c.Expr[String]): c.Expr[Any] = {
    import c.universe._

    val Literal(Constant(s_path: String)) = path.tree
    val controller = s_path.split('.').toList match {
      case _package :: controller :: method :: Nil =>      
      
       val controllerClass =  c.mirror.staticClass(s"${_package}.${controller}")
       val clazzInfo  =  controllerClass.info
       val publicMethods =   clazzInfo.decls.filterNot(_.isPrivate).filter(_.isMethod)

       val actionT = typeOf[Action[_]]

      publicMethods
      .filter(_.asMethod.typeSignature.resultType <:< actionT)
      .filter(_.asMethod.name.decodedName.toString == method)
      .flatMap(_.asMethod.typeSignature.resultType.typeArgs.headOption)
      
    }
    val x = JsonSchema.getJsonSchema(c)(controller.head)

    c.Expr(q"""${x}""")
  }
}
