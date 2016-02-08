package model

import spray.json
import spray.json._

/**
  * Created by Erik de Nooij on 8-2-2016.
  */
trait MyJsonProtocol extends json.DefaultJsonProtocol {
  implicit val neo4JRowFormat = jsonFormat1(Neo4JRow)
  implicit val columnsAndDataFormat = jsonFormat2(ColumnsAndData)
  implicit val neo4JResponseFormat = jsonFormat2(Neo4JResponse)

  implicit object AnyJsonFormat extends JsonFormat[Any] {
    def write(x: Any) = x match {
      case n: Int => JsNumber(n)
      case s: String => JsString(s)
      case b: Boolean if b == true => JsTrue
      case b: Boolean if b == false => JsFalse
    }
    def read(value: JsValue) = value match {
      case JsNumber(n) => n.intValue()
      case JsString(s) => s
      case JsTrue => true
      case JsFalse => false
    }
  }
}
