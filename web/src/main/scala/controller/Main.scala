package controller


import actors.Neo4jActor
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server._
import akka.pattern._
import akka.routing.RoundRobinPool
import akka.stream.ActorMaterializer
import akka.util.Timeout
import model.{Model, ResponseData}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

// Inspiration here: http://neo4j.com/docs/2.0/cypher-refcard/
object Main extends Config {


  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val timeout = Timeout(5.seconds)


  def main(args: Array[String]) {

    /*
       Parse the neo4jConfig file which is passed on via the command line
    */
    val neo4jConfig = Model.parseConfigFile(args)

    val neo4jRouter: ActorRef = system.actorOf(RoundRobinPool(1).props(Props (new Neo4jActor(neo4jConfig))), "neo4jRouter")


    val route: Route = {
      (requestContext: RequestContext) => {
        val futureResponse: Future[ResponseData] = (neo4jRouter ? requestContext.request).mapTo[ResponseData]

        futureResponse map { (neo4jResponse: ResponseData) =>
          RouteResult.Complete(HttpResponse(status = neo4jResponse.status,
            entity = HttpEntity(ContentTypes.`application/json`, neo4jResponse.body)))
        }
      }
    }

    Http().bindAndHandle(route, httpInterface, httpPort)
  }

}
