package schema.json

import org.scalatestplus.play.PlaySpec
import refined.OpenAPI
import play.api.libs.json._
class OpenAPISpec extends PlaySpec {


  "route with BasicDTO with a single field" must {
    "return a valid OpenAPI Spec" in {

      val r = OpenAPI.fromRoutesFile("./test/resources/CreateDTO.routes")
      (r \ "paths").as[JsObject] mustBe Json.parse(""" 
      {"/":{"post":{"parameters":[{"schema":{"type":"string"},"name":"nonEmptyString","in":"query","required":true}]}}}
      """)
    }
  }

  "route with TwoFieldsDTO with a two fields" must {
    "return a valid OpenAPI Spec" in {
      val r = OpenAPI.fromRoutesFile("./test/resources/TwoFields.routes")
      (r \ "paths").as[JsObject] mustBe Json.parse(""" 
      {"/":{"post":{"parameters":[
        {"schema":{"type":"int"},"name":"positiveInt","in":"query","required":true},
        {"schema":{"type":"array"},"name":"arrayOfString","in":"query","required":true}        
        ]}}}
      """)
    }
  }

}
