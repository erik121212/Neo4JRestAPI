package actors

import java.io.Serializable

import akka.http.scaladsl.model.HttpMethod

//import model.Model.{Neo4jJsonResponse}
import model.{Neo4JResponse, Model, ResponseData}
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils

/**
  * Created by Erik de Nooij on 6-2-2016.
  */
trait Neo4jHelper {

  /*
   * Helper function that converts a Cypher query statement into the Neo4J payload
   * @Param Serializable: a Cypher query statement, e.g.  "MATCH (c:Customer {clpnummer:674444}) RETURN (c)"
   *
   * Example return value: "{ "statements" : [ {  "statement" : "MATCH (c:Customer {clpnummer:674444})" } ]}"
   */
  def neo4jStatementToNeo4jRequestPayload(neo4jStatement: Serializable): ByteArrayEntity = {
    val neo4jRequestPayload = s"""{ "statements" : [ {  "statement" : "${neo4jStatement}" } ]}"""
    val entity = new ByteArrayEntity(neo4jRequestPayload.getBytes("UTF-8"))
    entity
  }


  /*
    * Helper function that converts a uri into the corresponding Cypher query statement or None if the url is not valid
    * @Param HttpMethod: the HTTP method, e.g. GET
    * @Param uriString: the url, e.g. customers/674444
    *
    * Example return value: "MATCH (c:Customer {clpnummer:674444}) RETURN (c)"
    */
  def urlToNeo4jStatement(method: HttpMethod, uriString: String): Option[String] = {
    method.value match {
      case "GET" => {
        uriString.split("/") match {
          case Array("customers", rgbnummer) => Some( s"""MATCH (cstmrs:Customer {rgbnummer:${rgbnummer}}) RETURN cstmrs""") // customers/671234
          case Array("customers", rgbnummer, "accounts") => Some( s"""MATCH (cstmrs:Customer {rgbnummer:${rgbnummer}})-[:Accountholder]->(accnts) RETURN cstmrs,accnts""") // customers/671234/accounts
          case Array("company", clpnummer) => Some( s"""MATCH (cmpny:Company {clpnummer:${clpnummer}}) RETURN cmpny""") // company/671234
          case Array("company", clpnummer, "accounts") => Some( s"""MATCH (cmpny:Company {clpnummer:${clpnummer}})-[:Accountholder]->(accnts)  RETURN cmpny,accnts""") // company/671234/accounts
          case _ => None
        }
      }
      case _ => None
    }
  }


  /*
  * Helper function that converts a Neo4J request payload into the corresponding Response data
  *
  * @Param ByteArrayEntity: the Neo4J request payload, e.g. "{ "statements" : [ {  "statement" : "MATCH (c:Customer {clpnummer:674444})" } ]}"
  *
  */
  def neo4jRequestPayloadToResponse(neo4jRequestPayload: ByteArrayEntity): ResponseData = {
    val url = "http://localhost:7474/db/data/transaction/commit";

    val post = new HttpPost(url)
    post.setEntity(neo4jRequestPayload)

    val client = new DefaultHttpClient
    val response = client.execute(post)
    val result = EntityUtils.toString(response.getEntity())

    val neo4JResponseObject = Model.convertNeo4JResponseStringToNeo4JResponseObject(result)

    val responseData = neo4JResponseObject.errors.size match {
      case 0 => {
        if (neo4JResponseObject.results(0).data.size == 0) {
          ResponseData(404, "404 Not Found")
        } else {
          ResponseData(200, result)
        }
      }
      case _ => ResponseData(500, neo4JResponseObject.errors(0))
    }

    responseData
  }


  /*
  * Function that returns a Response based on a request
  * @Param HttpMethod: the HTTP method, e.g. GET
  * @Param uriString: the url, e.g. customers/674444
  *
  */
  def requestToNeo4jResponse(method: HttpMethod, uriString: String): ResponseData = {

    val neo4jResponse = urlToNeo4jStatement(method, uriString) match {
      case Some(someNeo4jStatement) => {
        val neo4jRequestPayload = neo4jStatementToNeo4jRequestPayload(someNeo4jStatement)
        neo4jRequestPayloadToResponse(neo4jRequestPayload)
      }
      case None => ResponseData(404, "404 Not Found")
    }
    neo4jResponse
  }


}
