package computerdatabase

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class BasicSimulation extends Simulation {

	val httpProtocol = http
		.baseURL("http://cdn.jsdelivr.net")
		.inferHtmlResources(BlackList(), WhiteList())
		.acceptHeader("*/*")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-US,en;q=0.5")
		.connection("keep-alive")
		.userAgentHeader("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:33.0) Gecko/20100101 Firefox/33.0")

	val headers_1 = Map("Accept" -> "text/css,*/*;q=0.1")

    val uri1 = "http://www.trirand.com/blog/jqgrid"
    val uri2 = "cdn.jsdelivr.net"

	val scn = scenario("BasicSimulation")
		.exec(http("request_0")
			.get("/sockjs/0.3.4/sockjs.min.js")
			.resources(http("request_1")
			.get(uri1 + "/themes/ui.jqgrid.css")
			.headers(headers_1),
            http("request_2")
			.get(uri1 + "/themes/redmond/jquery-ui-custom.css")
			.headers(headers_1),
            http("request_3")
			.get(uri1 + "/js/i18n/grid.locale-en.js"),
            http("request_4")
			.get(uri1 + "/js/jquery.jqGrid.js"),
            http("request_5")
			.get(uri1 + "/js/jquery-ui-custom.min.js")))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}