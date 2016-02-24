package calendarsAPIperformanceTest

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._


import com.typesafe.scalalogging.StrictLogging
import io.gatling.http.cookie.CookieJar

import scala.util.{Try, Failure, Success}


class Neo4JSimulation extends Simulation with StrictLogging {

  //.baseURL("http://clrv0000002163.ic.ing.net/api")

  val httpProtocol = http
	//.baseURL("http://localhost:8080/api")
    .baseURL("http://localhost:9000")
    .inferHtmlResources()
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .connection("keep-alive")
    .userAgentHeader("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:35.0) Gecko/20100101 Firefox/35.0")
    .disableCaching
    .disableClientSharing

//http://localhost:9000/customers/674444
  // Scenario for user retrieving timeslots (GET)
  object GetCustomerData {
    val run =
      scenario("Gatling")
        .pause(99 milliseconds)
        .exec(http("GET customeer 674444")
        .get("/customers/674444")
        .check(status.in(200))
        )
  }


  val get_customers = scenario("GetCustomerData").repeat(2){exec(GetCustomerData.run)}

  setUp(
    get_customers.inject(rampUsers(100) over (20 seconds))
  ).protocols(httpProtocol)



}
