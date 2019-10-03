package schema.json

import org.scalatestplus.play.PlaySpec
import refined.OpenAPI
import play.api.libs.json._

class OpenAPISpec extends PlaySpec {


  "route with BasicDTO with a single field" must {
    "return a valid OpenAPI Spec" in {

      val r = OpenAPI.fromRoutesFile("./test/resources/CreateDTO.routes")
      (r \ "paths").as[JsObject] mustBe Json.parse(
        """
      {"/":{"post":{"parameters":[{"schema":{"type":"string"},"name":"nonEmptyString","in":"query"}]}}}
      """)
    }
  }


  "route with a single field : array of string" must {
    "return a valid OpenAPI Spec" in {

      val r = OpenAPI.fromRoutesFile("./test/resources/arrayOfString.routes")
      (r \ "paths").as[JsObject] mustBe Json.parse(
        """
      {"/":{"post":{"parameters":[{"schema":{"type":"array","items":{"type": "string"}},"name":"values","in":"query"}]}}}
      """)
    }
  }


  "route with TwoFieldsDTO with a two fields" must {
    "return a valid OpenAPI Spec" in {
      val r = OpenAPI.fromRoutesFile("./test/resources/TwoFields.routes")
      (r \ "paths").as[JsObject] mustBe Json.parse(
        """
      {"/":{"post":{"parameters":[
        {"schema":{"type":"integer","minimum":1},"in":"query","name":"positiveInt"},
        {"schema":{"type":"array","items":{"type":"string"}},"in":"query","name":"arrayOfString"}

        ]}}}
      """)
    }
  }

}
