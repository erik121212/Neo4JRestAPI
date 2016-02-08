package servicetest

import actors.Neo4jHelper

import akka.http.scaladsl.model.HttpMethod
import akka.http.javadsl.model._

/**
  * Created by Erik de Nooij on 7-2-2016.
  */
//class Neo4jHelperSpec extends UnitSpecBase with Neo4jHelper{
//
//
//  "A Response Object when" {
//    "bla " should {
//      "result in" {
//        val neo4jStatement = urlToNeo4jStatement(HttpMethod.custom("DELETE"), "")
//        neo4jStatement.sho
//      }
//    }
//  }
//}


/*

def urlToNeo4jStatement(method: HttpMethod, uriString: String): Serializable = {
      method.value match {
        case "GET" => {
          uriString.split("/") match {
            case Array("customers", rgbnummer) => s"""MATCH (cstmrs:Customer {rgbnummer:${rgbnummer}}) RETURN cstmrs"""     // customers/671234
            case Array("customers", rgbnummer, "accounts") => s"""MATCH (cstmrs:Customer {rgbnummer:${rgbnummer}})-[:Accountholder]->(accnts) RETURN cstmrs,accnts""" // customers/671234/accounts
            case Array("company", clpnummer) => s"""MATCH (cmpny:Company {clpnummer:${clpnummer}}) RETURN cmpny"""        // company/671234
            case Array("company", clpnummer, "accounts") => s"""MATCH (cmpny:Company {clpnummer:${clpnummer}})-[:Accountholder]->(accnts)  RETURN cmpny,accnts"""                 // company/671234/accounts
            case _ => None
          }
        }
        case _ => None
      }
    }
*/
