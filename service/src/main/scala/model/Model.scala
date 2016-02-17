package model

import akka.http.scaladsl.model.StatusCode
import spray.json._

import scala.io.Source

// ResponseData is the model being used to pass data on from the service layer to the weblayer
case class ResponseData(status: StatusCode, body: String)


case class Neo4JRow(row: List[Map[String, Any]])

case class ColumnsAndData(columns: List[String], data: List[Neo4JRow])

case class Neo4JResponse(results: List[ColumnsAndData], errors: List[String])

//case class Neo4jConfig(uriToCypherQueries: List[UriToCypherQuery])

case class API(httpMethod: String, uriString: String, headers: List[String])

case class APIToCypherQueriesMapping(api: API, cypherQuery: String)

case class Neo4jConfig(APIToCypherQueriesMapping: List[APIToCypherQueriesMapping])


object Model extends MyJsonProtocol {


  def parseConfigFile(args: Array[String]): Neo4jConfig = {

    val fileContents = Source.fromFile(args(0)).getLines.mkString

    val neo4jConfigTestJson = fileContents.parseJson
    neo4jConfigTestJson.convertTo[Neo4jConfig]
  }



  def convertNeo4JResponseStringToNeo4JResponseObject(neo4Jresponse: String): Neo4JResponse = {
    neo4Jresponse.parseJson.convertTo[Neo4JResponse]
  }

}

