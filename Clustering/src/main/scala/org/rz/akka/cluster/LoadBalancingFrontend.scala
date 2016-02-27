package org.rz.akka.cluster

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.routing.FromConfig
import com.typesafe.config.ConfigFactory
import org.rz.akka.cluster.Backend.Add


/**
  * Companion object for Load Balancing actor.
  */
object LoadBalancingFrontend {

  private var frontend: ActorRef = _

  val name: String = "LoadBalancingFrontendActor"

  /**
    * Initiates the frontend node system.
    */
  def initiate(): Unit ={
    val config = ConfigFactory.parseString("akka.cluster.roles=[frontend]").withFallback(ConfigFactory.load("loadbalancer"))
    val system = ActorSystem("ClusterSystem", config)
    frontend = system.actorOf(Props[LoadBalancingFrontend], name = name)
  }

  def getFrontend = frontend

}

/**
  * Frontend with load balancing via Akka metrics.
  */
class LoadBalancingFrontend extends Actor{

  val backend = context.actorOf(FromConfig.props(), name = LoadBalancingBackend.name)

  override def receive = {
    case op @ Add =>
      println(s"[FRONTEND] Add operation received, sending to backend (with load balancing)")
      backend forward op
    case _ => println("[FRONTEND] Unknown command")
  }
}