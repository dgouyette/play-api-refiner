package controllers

import org.dgouyette.play.bodyparsers.BodyParserWithJsonSchema
import domain.{ArrayOfStringDTO, BasicDTO, Blue, LoginDTO, SimpleDTO, TwoFieldsDTO}
import eu.timepit.refined.auto._
import javax.inject._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import org.dgouyette.refined.JsonSchema

@Singleton
class HomeController @Inject()(cc: ControllerComponents, bp: BodyParserWithJsonSchema, routesProvider: Provider[play.api.routing.Router]) extends AbstractController(cc) {


  def index(): Action[Unit] = Action(parse.empty) {
    implicit request =>
      Ok(Json.toJson(SimpleDTO("a key", 2, List(5), List("value"),Blue, BigDecimal(42))).as[JsObject])
  }

  implicit val simpleDTOfmt = SimpleDTO.fmt

  implicit val loginSchema = JsonSchema.asJsValue[LoginDTO]
  implicit val dtoSchema = JsonSchema.asJsValue[SimpleDTO]
  def login(): Action[LoginDTO] = Action(bp.jsonRefined(LoginDTO.fmt, loginSchema)) {
    implicit request =>Ok
  }

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
