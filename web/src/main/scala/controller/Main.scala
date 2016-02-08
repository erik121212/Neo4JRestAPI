package controller

import actors.Neo4jActor
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server._
import akka.pattern._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import model.ResponseData

//import model.Model.Neo4jResponse


import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

// Inspiration here: http://neo4j.com/docs/2.0/cypher-refcard/
object Main extends Config {


  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val timeout = Timeout(2.seconds)

  def main (args: Array[String]) {


  val neo4jActor = system.actorOf(Neo4jActor.props, "neo4jActor")

  val route: Route = {
    (requestContext: RequestContext) => {
      val futureResponse: Future[ResponseData] = (neo4jActor ? requestContext.request).mapTo[ResponseData]

      futureResponse map { (neo4jResponse: ResponseData) =>
        RouteResult.Complete(HttpResponse(status = neo4jResponse.status,
          entity = HttpEntity(ContentTypes.`application/json`, neo4jResponse.body)))
      }
    }
  }

  Http().bindAndHandle(route, httpInterface, httpPort)
  }

}