package org.dgouyette.refined
import java.io.File

import play.api.libs.json.JsValue
import play.api.mvc._
import play.routes.compiler.{HandlerCall, Route, RoutesFileParser}

import scala.io.Source
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

object OpenAPI {

  def fromRoutesFile(routePath: String) : Any = macro impl

  private def routes(routePath : String): Seq[Route] = {
    val lines = Source.fromFile(routePath).getLines()
      .filterNot(_.trim.isEmpty)
      .filterNot(_.trim.startsWith("#"))

    val file = new File(routePath)

    RoutesFileParser.parseContent(lines.mkString("\n"), file)
      .fold(
        _ => List(),
        rules => rules
      ).collect { case r: Route => r }
  }

  private def controllerPath(call : HandlerCall) : String =  s"${call.packageName.getOrElse("")}.${call.controller}"

  def impl(c: scala.reflect.macros.whitebox.Context)(routePath : c.Expr[String]): c.Expr[JsValue] = {
    import c.universe._
    val q"${r : String} " = routePath.tree
    val bodyTypes = routes(r).toList.flatMap(r =>getBodyType(c, r))
    val jsons = JsonSchema.openAPI(c)(bodyTypes)
    c.Expr(q"""${jsons}""")
  }

  private def getBodyType(c: whitebox.Context, r: Route): Option[(Route,c.universe.Type)] = {
    import c.universe._
    val controllerClass = c.mirror.staticClass(controllerPath(r.call))
    val clazzInfo = controllerClass.info
    val publicMethods = clazzInfo.decls.filterNot(_.isPrivate).filter(_.isMethod)

    val actionT = typeOf[Action[_]]

    publicMethods
      .filter(_.asMethod.typeSignature.resultType <:< actionT)
      .filter(_.asMethod.name.decodedName.toString == r.call.method)
      .flatMap(_.asMethod.typeSignature.resultType.typeArgs.headOption).headOption.map((r,_))
  }
}
