package actors

import akka.actor._
import akka.http.scaladsl.model.HttpRequest
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.{Http, http, Service}
import com.twitter.finagle.http.Request
import model.{ResponseData, Neo4jConfig}

object Neo4jActor {
  //val client: Service[http.Request, http.Response] = Http.newService("localhost:7474")
  //val client: Service[http.Request, http.Response] = Http.newService("localhost:9001,localhost:9002")

  val clientBuilder = ClientBuilder()
    .codec(com.twitter.finagle.http.Http())
    //.hosts("localhost:7373,localhost:7474")
    .hosts("localhost:9001,localhost:9002")
    .hostConnectionLimit(2)
   // .retries(3)

  val client2 = Http.client
    .withSessionPool.maxSize(2)
    .newService("localhost:7474")
//    .newService("localhost:9001,localhost:9002")

 // val client2 = clientBuilder.build()



  val neo4jRequest: Request = http.Request(http.Method.Post, "/db/data/transaction")
  neo4jRequest.host = "localhost"
}

class Neo4jActor(neo4jConfig: Neo4jConfig) extends Actor with Neo4jHelper with ActorLogging {


  override def receive: Receive = {

    case request: HttpRequest =>
     // sender ! requestToNeo4jResponse(request, neo4jConfig)

      eventualNeo4jResponse(sender, request, neo4jConfig, Neo4jActor.neo4jRequest, Neo4jActor.client2)
      //eventualNeo4jResponse(request, neo4jConfig, neo4jRequest, client) pipeTo sender
    case aresponse: ResponseData =>
      sender ! aresponse

    // Unknown_message
    case unknown_message =>
      log.info(s"Neo4jActor received the following unknown message: ${unknown_message.toString}")
  }
}
