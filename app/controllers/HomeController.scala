package controllers

import bodyParsers.DefaultBodyParsers
import domain.{ArrayOfStringDTO, BasicDTO, Blue, Red, SimpleDTO, TwoFieldsDTO}
import eu.timepit.refined.auto._
import javax.inject._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import refined.JsonSchema

@Singleton
class HomeController @Inject()(cc: ControllerComponents, bp: DefaultBodyParsers, routesProvider: Provider[play.api.routing.Router]) extends AbstractController(cc) {


  def index(): Action[Unit] = Action(parse.empty) {
    implicit request =>
      Ok(Json.toJson(SimpleDTO("a key", 2, List(5), List("value"),Blue, BigDecimal(42))).as[JsObject])
  }

  implicit val simpleDTOfmt = SimpleDTO.fmt
  implicit val dtoSchema = JsonSchema.asJsValue[SimpleDTO]

  def create(): Action[SimpleDTO] = Action(bp.jsonRefined(simpleDTOfmt, dtoSchema)) {
    implicit request =>Ok
  }

  def createWithNonEmptyString: Action[BasicDTO] = Action(bp.jsonRefined(BasicDTO.fmt, BasicDTO.schema)){
    implicit  req =>
      Ok
  }

  def createWithTwoFields: Action[TwoFieldsDTO] = Action(bp.jsonRefined(TwoFieldsDTO.fmt, TwoFieldsDTO.schema)){
    implicit  req =>
      Ok
  }

  def createArrayOfString: Action[ArrayOfStringDTO] = Action(bp.jsonRefined(ArrayOfStringDTO.fmt, ArrayOfStringDTO.schema)){
    implicit  req =>
      Ok
  }
}
