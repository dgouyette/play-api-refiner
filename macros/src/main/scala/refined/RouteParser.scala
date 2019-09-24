package refined
import java.io.File

import play.api.mvc._
import play.routes.compiler.{HandlerCall, Route, RoutesFileParser}

import scala.io.Source
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

object RouteParser {

  def bodyType: Any = macro impl

  lazy val routes: Seq[Route] = {

    val routeFile = "conf/routes"
    val lines = Source.fromFile(routeFile).getLines()
      .filterNot(_.trim.isEmpty)
      .filterNot(_.trim.startsWith("#"))

    val file = new File(routeFile)

    RoutesFileParser.parseContent(lines.mkString("\n"), file)
      .fold(
        _ => List(),
        rules => rules
      ).collect { case r: Route => r }
  }

  def controllerPath(call : HandlerCall) : String =  s"${call.packageName.getOrElse("")}.${call.controller}"

  def impl(c: scala.reflect.macros.whitebox.Context): c.Expr[Any] = {
    import c.universe._

    val controllers = routes.toList.flatMap(r => getBodyType(c, r.call))
    val jsons = controllers.map(controller => JsonSchema.getJsonSchema(c)(controller))
    println("jsons = "+ q"""..${jsons}""")
    c.Expr(q"""..${jsons}""")
  }

  private def getBodyType(c: whitebox.Context, handlerCall: HandlerCall): Iterable[c.universe.Type] = {
    import c.universe._


    val controllerClass = c.mirror.staticClass(controllerPath(handlerCall))
    val clazzInfo = controllerClass.info
    val publicMethods = clazzInfo.decls.filterNot(_.isPrivate).filter(_.isMethod)

    val actionT = typeOf[Action[_]]

    publicMethods
      .filter(_.asMethod.typeSignature.resultType <:< actionT)
      .filter(_.asMethod.name.decodedName.toString == handlerCall.method)
      .flatMap(_.asMethod.typeSignature.resultType.typeArgs.headOption)


  }
}
