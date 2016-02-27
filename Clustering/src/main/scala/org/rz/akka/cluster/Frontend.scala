package org.rz.akka.cluster

import akka.actor._
import com.typesafe.config.ConfigFactory
import org.rz.akka.cluster.Backend.{Add, BackendRegistration}

/**
  * Companion object for frontend actor node.
  */
object Frontend {

  private var frontend: ActorRef = _

  val name = "Frontend-Actor"

  /**
    * Initiates the frontend node system.
    */
  def initiate(): Unit ={
    val config = ConfigFactory.load.getConfig("FrontendNode")
    val system = ActorSystem("ClusterSystem", config)
    frontend = system.actorOf(Props[Frontend], name)
  }

  def getFrontend = frontend
}

/**
  * Frontend actor.
  */
class Frontend extends Actor {

  var backends = IndexedSeq.empty[ActorRef]

  override def receive = {
    case Add if backends.isEmpty =>
      println("[FRONTEND] No backend available for cluster")
    case op @ Add =>
      val chosenBackend: Int = util.Random.nextInt(backends.size)
      println(s"[FRONTEND] Add operation received, sending to random backend #$chosenBackend")
      backends(chosenBackend) forward op
    case BackendRegistration if !backends.contains(sender()) =>
      backends = backends :+ sender()
      context.watch(sender())
    case Terminated(actor) =>
      backends = backends.filterNot( _ == actor)
    case _ => println("[FRONTEND] Unknown command")
  }
}
