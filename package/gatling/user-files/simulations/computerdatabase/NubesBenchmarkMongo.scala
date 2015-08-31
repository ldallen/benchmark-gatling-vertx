package computerdatabase

import scala.concurrent.duration._
import scala.util.Random
import java.util.Calendar
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class NubesBenchmarkMongo extends Simulation {


	//config http générée par Gatling pour la connexion au serveur
	val httpProtocol = http
		.baseURL("http://localhost:8090")
		.proxy(Proxy("localhost", 8090).httpsPort(8090))
		.inferHtmlResources(BlackList(), WhiteList())
		.disableAutoReferer
		.shareConnections //quand un utilisateur a terminé, un autre peut utiliser sa connexion
		.acceptHeader("application/json")
		.acceptEncodingHeader("gzip, deflate, sdch")
		.acceptLanguageHeader("fr-FR,fr;q=0.8,en-US;q=0.6,en;q=0.4")
		.contentTypeHeader("application/json")
		.userAgentHeader("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.81 Safari/537.36")


	// Feeder personnalisé qui génère un jeu de données différent pour chaque user
	val userFeeder = new Feeder[String] {

	// instance du générateur d'aléatoires
	  private val RNG = new Random

	// Valeurs d'actions utilisées pour les requetes GET localhost:8090/api/todo/action/...
	  private val actions = Array("newtodo","test","code","new","work","buyasandwich","drinkcoffee","sleepabit")

	  // génère un nombre aléatoire entre a et b inclus
	  private def randInt(a:Int, b:Int) = RNG.nextInt(b-a+1) + a

	  // hasNext initialisé à true pour permettre des appels illimités à next
	  override def hasNext = true

	  // fonction appelée chaque fois qu'un user fait un feed(userFeeder)
	  override def next: Map[String, String] = {

	  	// action du Todo du post (générée avec la date actuelle)
	    val actionPostTask = Calendar.getInstance().getTime().toString + ":::action"

	    // Générations aléatoire des données à l'aide de la fonction randInt
	    val donePostTask = (randInt(1,2) == 1)

	    val actionUpdateTask = actions(randInt(0,actions.size - 1))

	    val doneUpdateTask = (randInt(1,2) == 1)

	    val doneGetDone = (randInt(1,2) == 1)

	    val actionGetAction = actions(randInt(0,actions.size - 1))

	    // La map dans laquelle le user va chercher les données
	    Map("actionPostTask" -> actionPostTask.toString,
	        "donePostTask" -> donePostTask.toString,
	        "actionUpdateTask" -> actionUpdateTask.toString,
	        "doneUpdateTask" -> doneUpdateTask.toString,
	        "doneGetDone" -> doneGetDone.toString,
	        "actionGetAction" -> actionGetAction.toString)
	    }
	}

	// Feeder pour la générations des temps de pause
	val pauseFeeder = new Feeder[Int] {

	  private val RNG = new Random

	  private def randInt(a:Int, b:Int) = RNG.nextInt(b-a+1) + a

	  override def hasNext = true

	  override def next: Map[String, Int] = {

	  	val pauseGetList = randInt(1,10)

	    val pausePostTask = randInt(1,10)

	    val pauseUpdateTask = randInt(1,10)

	    val pauseGetDone = randInt(1,10)

	    val pauseGetAction = randInt(1,10)

	    Map("pauseGetList" -> pauseGetList,
	        "pausePostTask" -> pausePostTask,
	        "pauseUpdateTask" -> pauseUpdateTask,
	        "pauseGetDone" -> pauseGetDone,
	        "pauseGetAction" -> pauseGetAction)
	    }
	}

	// Requete qui renvoie la liste des taches avec une pause aléatoire
	object GetList {

		val getList = feed(pauseFeeder).exec(http("Get list")
			.get("/api/todos"))
			.pause("${pauseGetList}")

	}

	// Requete qui post une nouvelle tache générée par le feeder + pause aléatoire
	object PostTask { 

		val postTask = feed(userFeeder).feed(pauseFeeder).exec(http("Post task")
			.post("/api/todo")
			.body(StringBody("""{"action":"${actionPostTask}","done":${donePostTask}}""")))
			.pause("${pausePostTask}")
	}

	// Requete qui fait une mise à jour du statut d'une tache (toujours aléatoire)
	object UpdateTask {

		val updateTask = feed(userFeeder).feed(pauseFeeder).exec(http("Update task")
			.put("/api/todo/${actionUpdateTask}")
			.body(StringBody("""{"done":${doneUpdateTask}}""")))
			.pause("${pauseUpdateTask}")

	}

	// Requete qui récupère toutes les taches pour un certain statut (aléatoire)
	object GetDone {

		val getDone = feed(userFeeder).feed(pauseFeeder).exec(http("Get status")
			.get("/api/todo/done/${doneGetDone}"))
			.pause("${pauseGetDone}")
	}

	// Requete qui récupère toutes les taches pour une certaine action (aléatoire)
	object GetAction {

		val getAction = feed(userFeeder).feed(pauseFeeder).exec(http("Get action")
			.get("/api/todo/action/${actionGetAction}"))
			.pause("${pauseGetAction}")
	}

	// définition du scénario à partir des objets définis auparavant
	val nubes = scenario("NubesBenchmark").exec(GetList.getList, PostTask.postTask, UpdateTask.updateTask, GetDone.getDone, GetAction.getAction)

	// On utilise rampUser pour une montée en charge progressive du nombre d'utilisateurs (limite = 60k simultanées = nbre de sockets max)
setUp(
		nubes.inject(rampUsers(50000) over (20 seconds)).throttle(reachRps(1000) in (1 second), holdFor(4 minutes)).disablePauses).protocols(httpProtocol)

}