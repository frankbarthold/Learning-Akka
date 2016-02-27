package org.rz.akka.cluster

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import org.rz.akka.cluster.LoadBalancingBackend.Add

/**
  * Companion object for Load Balancing Backend actor.
  */
object LoadBalancingBackend {

  sealed trait ClusterOperation
  case class BackendRegistration() extends ClusterOperation
  case class Add() extends ClusterOperation

  val name: String = "BackendRouter"

  /**
    * Initiates the backend node system.
    *
    * @param port The port where the backend actor system will be bounded to.
    */
  def initiate(port: Int): Unit ={
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
        .withFallback(ConfigFactory.parseString("akka.cluster.roles=[backend]"))
        .withFallback(ConfigFactory.load("loadbalancer"))
    val system = ActorSystem("RemoteLoadBalancedSystem", config)
    system.actorOf(Props[LoadBalancingBackend], name = name)
  }
}

/**
  * Actor that will run at the backend node.
  */
class LoadBalancingBackend extends Actor {

  override def receive = {
    case Add => println(s"[BACKEND] Frontend ${sender()} pinged us!")
    case default => println(s"[BACKEND] Unknown command $default")
  }

}
