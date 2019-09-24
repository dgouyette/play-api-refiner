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

  def controllerPath(call : HandlerCall) : String =  s"${call.packageName.getOrElse("")}.${call.controller}.${call.method}"

  def impl(c: scala.reflect.macros.whitebox.Context): c.Expr[Any] = {
    import c.universe._

    val s_path: String = controllerPath(routes.head.call)
    val paths: Seq[String] = routes.map(r => controllerPath(r.call))
    val controllers: Seq[c.universe.Type] = paths.flatMap(p => getBodyType(c, s_path))
    val jsons: Seq[c.Expr[String]] = controllers.map(controller => JsonSchema.getJsonSchema(c)(controller))
    jsons.foreach(println)
    c.Expr(q"""..${jsons}""")
  }

  private def getBodyType(c: whitebox.Context, s_path: String): Iterable[c.universe.Type] = {
    import c.universe._
    val controller = s_path.split('.').toList match {
      case _package :: controller :: method :: Nil =>

        val controllerClass = c.mirror.staticClass(s"${_package}.${controller}")
        val clazzInfo = controllerClass.info
        val publicMethods = clazzInfo.decls.filterNot(_.isPrivate).filter(_.isMethod)

        val actionT = typeOf[Action[_]]

        publicMethods
          .filter(_.asMethod.typeSignature.resultType <:< actionT)
          .filter(_.asMethod.name.decodedName.toString == method)
          .flatMap(_.asMethod.typeSignature.resultType.typeArgs.headOption)

    }
    controller
  }
}
