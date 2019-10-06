package schema.json

import org.scalatestplus.play.PlaySpec
import refined.OpenAPI
import play.api.libs.json._

class OpenAPISpec extends PlaySpec {


  "route with BasicDTO with a single field" must {
    "return a valid OpenAPI Spec" in {

      val r : JsValue = OpenAPI.fromRoutesFile("./test/resources/CreateDTO.routes")
      r mustBe Json.parse(
        """
      {
      "components":{
        "schemas":{
            "post_":{
              "type":"object",
              "properties":
                {"nonEmptyString":{"minLength":1,"type":"string"}}}}},
      "openapi":"3.0.0",
      "paths":
        {
          "/":{"post":{
            "requestBody":{"content":{"application/json":{"schema":{"$ref":"#/components/schemas/post_"}}}},
            "responses":{"200":{"description":"OK"}}}}},"info":{"title":"API","version":"v1"}}
      """)
    }
  }


  "route with a single field : array of string" must {
    "return a valid OpenAPI Spec" in {

      val r:JsValue = OpenAPI.fromRoutesFile("./test/resources/arrayOfString.routes")
      r mustBe Json.parse(
        """
      {
        "components":
          {"schemas":{"post_":{"type":"object","properties":{"values":{"type":"array","items":{"type":"string"}}}}}},
        "openapi":"3.0.0",
        "paths":{
          "/":
            {"post":
              {
                "requestBody":{"content":{"application/json":{"schema":{"$ref":"#/components/schemas/post_"}}}},
                "responses":{"200":{"description":"OK"}}}}},"info":{"title":"API","version":"v1"}}
      """)
    }
  }


  "route with TwoFieldsDTO with a two fields" must {
    "return a valid OpenAPI Spec" in {
      val r : JsValue = OpenAPI.fromRoutesFile("./test/resources/TwoFields.routes")
      r mustBe Json.parse(
        """
      {
        "components":
          {"schemas":{
            "post_":{"type":"object","properties":{"arrayOfString":{"type":"array","items":{"type":"string"}},"positiveInt":{"minValue":1,"type":"integer"}}}}},
        "openapi":"3.0.0",
        "paths":{
          "/":
            {"post":{
              "requestBody":{"content":{"application/json":{"schema":{"$ref":"#/components/schemas/post_"}}}},
              "responses":{"200":{"description":"OK"}}}}},"info":{"title":"API","version":"v1"}}
      """)
    }
  }

}
