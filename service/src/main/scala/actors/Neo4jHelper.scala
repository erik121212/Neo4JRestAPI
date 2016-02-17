package actors

import java.io.Serializable

import akka.http.scaladsl.model.{StatusCodes, HttpRequest}

import model._
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
    val neo4jRequestPayload = s"""{ "statements" : [ {  "statement" : ${neo4jStatement} } ]}"""

    val entity = new ByteArrayEntity(neo4jRequestPayload.getBytes("UTF-8"))
    entity
  }


  /*
  * helper function that checks if two segments are different where a segment may contain a parameter
  *
  * The return value is true if the segments are not equal
  *
  * @Param pairOfSegments: a pair of segments: e.g. (customers,customers) or (674444,$PARAM1)
  *
  */
  def segmentsAreDifferent(pairOfSegments: (String, String)) = {
    if (pairOfSegments._1 == pairOfSegments._2)
      false
    else
      !(!pairOfSegments._1.isEmpty && pairOfSegments._2.startsWith("$PARAM"))
  }


  /*
  * helper function that checks if two uri's are equal
  *
  * The return value is true if the uri's are equal else false:
  *
  * @Param aUriString:              the uri of the API call,               e.g. customers/674444/accounts
  * @Param aParameterizedUriString: the uri that might contain parameters, e.g. customers\$PARAM1
  *
  */
  def urisAreEqual(aUriString: String, aParameterizedUriString: String) = {
    val aUriStringSegments = aUriString.split('/')
    val aParameterizedUriStringSegments = aParameterizedUriString.split('\\')

    if (aUriStringSegments.size != aParameterizedUriStringSegments.size)
      false
    else {
      val pairOfSegments: List[(String, String)] = aUriStringSegments.toList zip aParameterizedUriStringSegments.toList
      !pairOfSegments.exists(segmentsAreDifferent)
    }
  }

  /*
 * helper function that checks if the currentAPICall is mapped to a cypher query
 *
 * The return value is true if:
 *    The httpMethod is the same
 *    The signature of the Url is the same
 *
 * @Param currentAPICall: an API call, e.g. API(GET,customers/674444/accounts,List())
 * @Param apiMapping: a mapping between an API and a cypher query
 *
 */
  def mappingExists(currentAPICall: API, apiMapping: APIToCypherQueriesMapping) = {
    if (!(apiMapping.api.httpMethod == currentAPICall.httpMethod))
      false
    else {
      urisAreEqual(currentAPICall.uriString, apiMapping.api.uriString)
    }
  }


  /*
  * Helper function that checks if the uri segment contains a parameter
  *
  * @Param segmentCurrentAPI: a segment of an Uri
  * @Param segmentMappingAPI: a segment of an Uri that may contain a parameter
  *
  */
  def containsParameter(segmentCurrentAPI: String, segmentMappingAPI: String) = {

    if (segmentMappingAPI.startsWith("$PARAM")) {
      (segmentMappingAPI, segmentCurrentAPI)
    } else
      None
  }


  /*
  * Helper function that gets a parameterized Cypher statement and returns a Cypher query in which the parameters are resolved
  *
  * @Param cypherStatement:     a parameterized Cypher statement, e.g. "MATCH (cstmrs:Customer {rgbnummer:{rgbnummer}}) -[:Accountholder]->(accnts) RETURN cstmrs,accnts","parameters" : {"rgbnummer" : $PARAM1}
  * @Param parameterValuePair:  mapping of all parameters with their current values, e.g. List((674444,$PARAM1))
  *
  */
  def resolveCypherQuery(cypherStatement: String, parameterValuePair: List[(String, String)]): String = {
    parameterValuePair match {
      case Nil => cypherStatement
      case _ :: tail => {
        // TODO Refactor following two lines of ugly code
        val beginIndex = cypherStatement.indexOf("$PARAM")
        val newCypherStatement = cypherStatement.substring(0, beginIndex - 1) + parameterValuePair(0)._1 + cypherStatement.substring(beginIndex + "$PARAMX".length)

        resolveCypherQuery(newCypherStatement, parameterValuePair.tail)
      }
    }
  }

  /*
  * Helper function that returns a Cypher query based on an API call and its mapping
  *
  * The return value is a Cypher statement
  *
  * @Param currentAPICall: the current API call
  * @Param mapping:        mapping for this API call
  *
  */
  def getCypherStatement(currentAPICall: API, mapping: APIToCypherQueriesMapping) = {
    val currentAPIsegments = currentAPICall.uriString.split('/')
    val mappedAPIsegments = mapping.api.uriString.split('\\')

    val pairOfSegments = currentAPIsegments.toList zip mappedAPIsegments.toList
    val pairContainingParameters = pairOfSegments.filter(pairOfSegment => pairOfSegment._2 startsWith ("$PARAM"))

    Some(resolveCypherQuery(mapping.cypherQuery, pairContainingParameters))
  }


  /*
  * Helper function that converts a Neo4J request payload into the corresponding Response data
  *
  * @Param ByteArrayEntity: the Neo4J request payload, e.g. "{ "statements" : [ {  "statement" : "MATCH (c:Customer {clpnummer:674444})" } ]}"
  *
  */
  def neo4jRequestPayloadToResponse(method: String, neo4jRequestPayload: ByteArrayEntity): ResponseData = {
    val url = "http://localhost:7474/db/data/transaction/commit"

    val post = new HttpPost(url)
    post.setEntity(neo4jRequestPayload)

    val client = new DefaultHttpClient
    val response = client.execute(post)
    val result = EntityUtils.toString(response.getEntity())


    val neo4JResponseObject = Model.convertNeo4JResponseStringToNeo4JResponseObject(result)

    val responseData = neo4JResponseObject.errors.size match {
      case 0 => {
        method match {
          case "GET" => {
            if (neo4JResponseObject.results(0).data.size == 0) {
              ResponseData(StatusCodes.NotFound, "404 Not Found")
            } else {
              ResponseData(StatusCodes.OK, result)
            }
          }
          case "POST" => ResponseData(StatusCodes.Created, result)
          case "PUT" => ResponseData(StatusCodes.OK, result)
          case "DELETE" => ResponseData(StatusCodes.OK, result)
          case _ => ResponseData(StatusCodes.BadRequest, "Bad request")
        }

      }
      case _ => ResponseData(StatusCodes.InternalServerError, neo4JResponseObject.errors(0))
    }

    responseData
  }


  /*
  * Helper function that converts a HttpRequest into the corresponding Cypher query statement or None if the url is not valid
  *
  * @Param request: the HttpRequest
  * @Param neo4jConfig: the configuration of all mappings
  *
  * Example return value: "MATCH (c:Customer {clpnummer:674444}) RETURN (c)"
  */

  def urlToCypherStatement(request: HttpRequest, neo4jConfig: Neo4jConfig): Option[String] = {
    val currentAPICall = API(request.method.value, request.uri.path.tail.toString(), List())

    val mappedCypherQuery = neo4jConfig.APIToCypherQueriesMapping.find(apiMapping => mappingExists(currentAPICall, apiMapping))

    mappedCypherQuery match {
      case Some(existingAPIToCypherQueriesMapping) => getCypherStatement(currentAPICall, existingAPIToCypherQueriesMapping)
      case _ => None
    }

  }


  /*
  * Function that returns ResponseData  based on a HttpRequest
  *
  * @Param request: the HttpRequest
  * @Param neo4jConfig: the configuration of all mappings
  *
  */
  def requestToNeo4jResponse(request: HttpRequest, neo4jConfig: Neo4jConfig): ResponseData = {

    val neo4jResponse = urlToCypherStatement(request, neo4jConfig) match {
      case Some(someNeo4jStatement) => {
        println(someNeo4jStatement)
        val neo4jRequestPayload = neo4jStatementToNeo4jRequestPayload(someNeo4jStatement)
        neo4jRequestPayloadToResponse(request.method.value, neo4jRequestPayload)
      }
      case None => ResponseData(404, "404 Not Found")
    }
    neo4jResponse
  }


}
