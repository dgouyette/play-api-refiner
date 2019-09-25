package schema.json

import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean._
import eu.timepit.refined.collection._
import eu.timepit.refined.numeric._
import eu.timepit.refined.string._
import org.scalatestplus.play._
import play.api.libs.json._
import refined.JsonSchema
import shapeless.nat._


class StackSpec extends PlaySpec {

  case class SimpleString(a : String)
  case class SimpleOptionString(aa : Option[String])
  case class SimpleInt(b : Int)
  case class SimpleNumbers(a : BigDecimal, b : Double, c : Float)
  case class IntRefinedPositive(c : Int Refined Positive)
  case class StringNonEmpty(d : String Refined NonEmpty)
  case class CollectionStringNonEmpty(e : List[String] Refined NonEmpty)
  case class CollectionIntNonEmpty(ee : List[Int] Refined NonEmpty)
  case class CollectionStringNotRefined(eee : List[String])
  case class CollectionIntNotRefined(eee : List[Int])
  
  sealed trait TrafficLight
  case object Red extends TrafficLight
  case object Orange extends TrafficLight
  case object Green extends TrafficLight
  case class SimpleEnum(e : TrafficLight)


  case class StringFormatIPV4(f : String Refined IPv4)
  case class StringFormatIPV6(g : String Refined IPv6)
  case class StringFormatURI(h : String Refined Uri)

  case class ClassWithTwoFields(i : String Refined NonEmpty, j : Int Refined Positive)
  case class StringWithMinSizeZero(k: String Refined MinSize[_0])
  case class StringWithMinSize22(k: String Refined MinSize[_22])
  case class StringWithMaxSize22(l: String Refined MaxSize[_22])
  case class StringWithMinMax(m: String Refined (MinSize[_0] And MaxSize[_5]))

  "String not refined" must {
    "return a schema with type String" in {
      JsonSchema.jsonSchema[SimpleString] mustBe Json.obj("a"->Json.obj("type"-> "string"))
    }
  }

  "Int not refined" must {
    "return a schema with type Int" in {
      JsonSchema.jsonSchema[SimpleInt] mustBe  Json.parse("""{"b":{"type":"integer"}}""")
    }
  }

  "BigDecimal not refined" must {
    "return a schema with type number" in {
      JsonSchema.jsonSchema[SimpleNumbers] mustBe Json.parse("""{"a":{"type":"number"},"b":{"type":"number"},"c":{"type":"number"}}""")
    }
  }

  "Int refined Positive" must {
    "return a schema with type Int" in {
      JsonSchema.jsonSchema[IntRefinedPositive] mustBe Json.parse("""{"c":{"minValue":1,"type":"integer"}}""")
    }
  }

  "String Refined NonEmpty" must {
    "return a schema with type String and minLength" in {
      JsonSchema.jsonSchema[StringNonEmpty] mustBe Json.parse("""{"d":{"minLength":1,"type":"string"}}""")
    }
  }

  "Collection[String] Refined NonEmpty" must {
    "return a schema with type Collection[String] and minLength" in {
      JsonSchema.jsonSchema[CollectionStringNonEmpty] mustBe Json.parse("""{"e":{"minLength":1,"type":"array","items":{"type":"string"}}}""")
    }
  }

  "Collection[Int] " must {
    "return a schema with type Collection[Int] " in {
      JsonSchema.jsonSchema[CollectionIntNonEmpty] mustBe Json.parse("""{"ee":{"minLength":1,"type":"array","items":{"type":"integer"}}}""")
    }
  }


"Collection[String] with no Refined " must {
  "return a schema with type array " in {
    JsonSchema.jsonSchema[CollectionStringNotRefined] mustBe Json.parse("""{"eee":{"type":"array","items":{"type":"string"}}}""")
  }
}

"Collection[Int] with no Refined " must {
  "return a schema with type array " in {
    JsonSchema.jsonSchema[CollectionIntNotRefined] mustBe Json.parse("""{"eee":{"type":"array","items":{"type":"integer"}}}""")
  }
}

  "String Refined IPv4" must {
    "return a schema with type String and format = ipv4" in {
      JsonSchema.jsonSchema[StringFormatIPV4] mustBe Json.parse("""{"f":{"format":"ipv4","type":"string"}}""")
    }
  }

  "String Refined IPv6" must {
    "return a schema with type String and format = ipv6" in {
      JsonSchema.jsonSchema[StringFormatIPV6] mustBe Json.parse("""{"g":{"format":"ipv6","type":"string"}}""")
    }
  }

  "String Refined URI" must {
    "return a schema with type String and format = uri" in {
      JsonSchema.jsonSchema[StringFormatURI] mustBe Json.parse("""{"h":{"format":"uri","type":"string"}}""")
    }
  }

  "case class with multiples fields" must {
    "return a schema with all the fields" in {
      JsonSchema.jsonSchema[ClassWithTwoFields] mustBe Json.parse("""{"i":{"minLength":1,"type":"string"},"j":{"minValue":1,"type":"integer"}}""")
    }
  }

  "String with min size" must {
    "return a schema with min size" in {
      JsonSchema.jsonSchema[StringWithMinSizeZero] mustBe Json.parse("""{"k":{"minLength":0,"type":"string"}}""")
    }  
  }


  "String with min size 22" must {
    "return a schema with min size" in {
      JsonSchema.jsonSchema[StringWithMinSize22] mustBe Json.parse("""{"k":{"minLength":22,"type":"string"}}""")
    }  
  }
  
  "String with max size 22" must {
    "return a schema with max size" in {
      JsonSchema.jsonSchema[StringWithMaxSize22] mustBe Json.parse("""{"l":{"type":"string","maxLength":22}}""")
    }  
  }

 "String with min and max size" must {
    "return a schema with min and max size" in {
      JsonSchema.jsonSchema[StringWithMinMax] mustBe Json.parse("""{"m":{"minLength":0,"type":"string","maxLength":5}}""")
    }  
  }

  "sealed trait with object" must {
    "return enum with all value as a string" in {
      JsonSchema.jsonSchema[SimpleEnum] mustBe Json.parse("""{"e":{"enum":["red","orange","green"],"type":"string"}}""")
    }  
  }

}