package org.rz.akka.cluster.loadbalanced

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.routing.FromConfig
import com.typesafe.config.ConfigFactory
import org.rz.akka.cluster.loadbalanced.LoadBalancingBackend.Add

import scala.concurrent.duration.FiniteDuration


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
    val system = ActorSystem("RemoteLoadBalancedSystem", config)
    frontend = system.actorOf(Props[LoadBalancingFrontend], name = name)
  }

  def getFrontend = frontend

}

/**
  * Frontend with load balancing via Akka metrics.
  */
class LoadBalancingFrontend extends Actor{

  import scala.concurrent.ExecutionContext.Implicits.global

  val backend = context.actorOf(FromConfig.props(), name = LoadBalancingBackend.name)
  context.system.scheduler.schedule(FiniteDuration(10, TimeUnit.SECONDS), FiniteDuration(10, TimeUnit.SECONDS), self, Add)

  override def receive = {
    case op @ Add =>
      println(s"[FRONTEND] Add operation received, sending to backend $backend")
      backend forward op
    case default => println(s"[FRONTEND] Unknown command $default")
  }
}