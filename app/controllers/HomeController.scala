package controllers

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import javax.inject._
import play.api._
import play.api.mvc._
import domain.SimpleDTO
import bodyParsers.DefaultBodyParsers

@Singleton
class HomeController @Inject()(cc: ControllerComponents, bp : DefaultBodyParsers) extends AbstractController(cc) {

  
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(Json.toJson(SimpleDTO("a key", 2)))
  }

  def create() = Action(bp.json(SimpleDTO.fmt)) {
    implicit request => 
    Created
  }
}
