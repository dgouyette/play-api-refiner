package controllers


import javax.inject._
import play.api.mvc._

@Singleton
class SwaggerController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def home: Action[Unit] = Action(parse.empty) {
    implicit  req =>
      Ok(refined.RouteParser.bodyType)

  }
}