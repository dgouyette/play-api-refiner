package controllers


import bodyParsers.DefaultBodyParsers
import domain.SimpleDTO
import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._

object SwaggerRefined {

  type HandlerDTO[T] = Handler
  type RefinedRoutes = PartialFunction[RequestHeader, HandlerDTO[SimpleDTO]]

  case class RefinedRoute[T](h: Handler, actionType: Action[T])

  case class Route(method: String, path: String, schema: String)

  implicit val fmt = Json.format[Route]
}

@Singleton
class SwaggerController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {


  def home = Action {
      Ok(refined.RouteParser.bodyType)

  }
}