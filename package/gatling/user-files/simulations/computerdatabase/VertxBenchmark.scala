package computerdatabase

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class VertxBenchmark extends Simulation {

	val httpProtocol = http
		.baseURL("http://localhost:8090")
		.proxy(Proxy("localhost", 8090).httpsPort(8090))
		.inferHtmlResources(BlackList(), WhiteList())
		.disableAutoReferer
		.acceptHeader("application/json")
		.acceptEncodingHeader("gzip, deflate, sdch")
		.acceptLanguageHeader("fr-FR,fr;q=0.8,en-US;q=0.6,en;q=0.4")
		.contentTypeHeader("application/x-www-form-urlencoded")
		.userAgentHeader("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.81 Safari/537.36")

    val uri1 = "http://localhost:8090/api"

	val scn = scenario("VertxBenchmark")
		.exec(http("request_0")
			.get("/api/todos"))
		.pause(1)
		.exec(http("request_1")
			.post("/api/todo")
			.body(StringBody("""{"action":"newTodo","done":false}""")))
		.pause(1)
		.exec(http("request_2")
			.put("/api/todo/1")
			.body(StringBody("""true""")))
		.pause(1)
		.exec(http("request_3")
			.get("/api/todo/view/1"))
		.pause(1)
		.exec(http("request_4")
			.get("/api/todo/action/new"))
		.pause(1)
		.exec(http("request_5")
			.get("/api/todo/done/true"))

	setUp(scn.inject(rampUsers(8000) over (1 seconds))).protocols(httpProtocol)
}