package controllers


import bodyParsers.DefaultBodyParsers
import domain.SimpleDTO
import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
object SwaggerRefined {

    type HandlerDTO[T] = Handler
    type RefinedRoutes = PartialFunction[RequestHeader, HandlerDTO[SimpleDTO]]

  case class RefinedRoute[T](h : Handler, actionType : Action[T])
  case class Route(a : String, b : String, c : String)
  implicit val fmt = Json.format[Route]
}

@Singleton
class SwaggerController @Inject()(cc: ControllerComponents, bp: DefaultBodyParsers, routesProvider: Provider[play.api.routing.Router],homeController: HomeController) extends AbstractController(cc) {


  def home = Action {
    implicit r =>
      val routes: Seq[SwaggerRefined.Route] = routesProvider.get().documentation.map(SwaggerRefined.Route.tupled(_))
      val json = Json.obj(
      //  "index"->  refined.RouteParser.bodyType("controllers.HomeController.index").toString(),
        "create"-> refined.RouteParser.bodyType("controllers.HomeController.create").toString()
        )
      Ok(json)

  }
}