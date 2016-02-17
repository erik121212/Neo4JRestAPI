package actors

import akka.actor._
import akka.http.scaladsl.model.HttpRequest
import model.{ResponseData, Neo4jConfig}


case class Neo4jActor(neo4jConfig: Neo4jConfig) extends Actor with Neo4jHelper with ActorLogging {

  override def receive: Receive = {

    case request: HttpRequest =>
//      sender ! requestToNeo4jResponse(request.method, request.uri.path.tail.toString())
      sender ! requestToNeo4jResponse(request, neo4jConfig)

    // Unknown_message
    case unknown_message =>
      log.info(s"Neo4jActor received the following unknown message: ${unknown_message.toString}")
  }
}