package org.dgouyette.play.bodyparsers

import akka.stream.Materializer
import javax.inject._
import play.api.http.{HttpErrorHandler, ParserConfiguration}
import play.api.libs.Files.TemporaryFileCreator
import play.api.libs.json.{JsError, JsValue, Json, Reads}
import play.api.mvc.{BodyParser, PlayBodyParsers, Results}

import scala.concurrent.{ExecutionContext, Future}

class BodyParserWithJsonSchema @Inject()(
   val materializer: Materializer,
   val config: ParserConfiguration,
   val temporaryFileCreator: TemporaryFileCreator,
   val errorHandler : HttpErrorHandler
  )  extends PlayBodyParsers {

  def jsonRefined[A](implicit reader: Reads[A], schema : JsValue): BodyParser[A] = {

    BodyParser("json reader") { request =>
      implicit val ec = ExecutionContext.global
      json(request) mapFuture {
        case Left(simpleResult) =>
          Future.successful(Left(simpleResult))
        case Right(jsValue) =>
          jsValue.validate(reader) map { a =>
            Future.successful(Right(a))
          } recoverTotal { jsError =>
                val error = JsError.toJson(jsError) ++ Json.obj("_schema" -> schema)
                Future.successful(Left(Results.BadRequest(error)))
          }
      }
    }
  }
}