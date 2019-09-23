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
import refined.JsonSchema
import domain.Red

@Singleton
class HomeController @Inject()(cc: ControllerComponents, bp : DefaultBodyParsers, routesProvider: Provider[play.api.routing.Router]) extends AbstractController(cc) {
   
  implicit val dtoSchema: JsonSchema =  JsonSchema(JsonSchema.jsonSchema[SimpleDTO])
  
  def index() : Action[Unit] = Action(parse.empty) { implicit request =>
    Ok(Json.toJson(SimpleDTO("a key", 2,List(5), List("value"), Red, BigDecimal(42) )).as[JsObject])
  }

  def create(): Action[SimpleDTO] = Action(bp.jsonRefined(SimpleDTO.fmt,dtoSchema)) {
    implicit request =>
    Created
  }
}
