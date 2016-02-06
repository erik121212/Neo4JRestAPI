package actors

import akka.actor._
import akka.http.scaladsl.model.HttpRequest



object Neo4jActor {
  def props: Props = Props[Neo4jActor]
  //def props(senderActorRef: ActorRef): Props = Props[Neo4jActor]
}

//class Neo4jActor(senderActorRef: ActorRef) extends Actor with Neo4jHelper with ActorLogging {
class Neo4jActor extends Actor with Neo4jHelper with ActorLogging {

  override def receive: Receive = {

    case request: HttpRequest =>
      sender ! requestToNeo4jResponse(request.method, request.uri.path.tail.toString())

    // Unknown_message
    case unknown_message =>
      log.info(s"Neo4jActor received the following unknown message: ${unknown_message.toString}")
  }
}