package controllers


import javax.inject._
import org.dgouyette.refined.OpenAPI
import play.api.mvc._

@Singleton
class OpenAPIController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def home: Action[Unit] = Action(parse.empty) {
    implicit  req =>
    Ok(views.html.Application.index())
  }

  def json: Action[Unit] = Action(parse.empty) {
    implicit  req =>
     Ok(OpenAPI.fromRoutesFile("conf/routes"))
  }
}