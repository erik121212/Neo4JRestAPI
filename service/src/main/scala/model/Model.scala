package model

//import spray.json.DefaultJsonProtocol
import typedefinitions._

//case class Neo4jResponse(status: StatusCode, body: String)

//object Model {//extends DefaultJsonProtocol  {
//
//
// // case class JsonFilesConfig(stubs: Array[String])
//
case class Neo4jResponse(status: StatusCode, body: String)
//
//  // Note: these implicits must tbe declared in the correct order (first the leaves, then the composing classes)
//  //implicit val castaliaStatusResponseFormatter = jsonFormat1(CastaliaStatusResponse)
//}