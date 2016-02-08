package model

import spray.json._
import typedefinitions._


case class ResponseData(status: StatusCode, body: String)

case class Neo4JRow(row: List[Map[String, Any]])
case class ColumnsAndData(columns: List[String], data: List[Neo4JRow])
case class Neo4JResponse(results: List[ColumnsAndData], errors: List[String])

object Model extends MyJsonProtocol {

  def convertNeo4JResponseStringToNeo4JResponseObject(neo4Jresponse : String) : Neo4JResponse = {
    neo4Jresponse.parseJson.convertTo[Neo4JResponse]
  }

}

