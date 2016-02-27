package org.rz.akka.cluster

import akka.actor.{Actor, ActorSystem, Props, RootActorPath}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.MemberUp
import com.typesafe.config.ConfigFactory
import org.rz.akka.cluster.Backend.{Add, BackendRegistration}

/**
  * Companion object for Backend actor.
  */
object Backend {

  sealed trait ClusterOperation
  case class BackendRegistration() extends ClusterOperation
  case class Add() extends ClusterOperation

  /**
    * Initiates the backend node system.
    *
    * @param port The port where the backend actor system will be bounded to.
    */
  def initiate(port: Int): Unit ={
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").withFallback(ConfigFactory.load.getConfig("BackendNode"))
    val system = ActorSystem("ClusterSystem", config)
    system.actorOf(Props[Backend], "Backend-Actor")
  }
}

/**
  * Actor that will run at the backend node.
  */
class Backend extends Actor {

  val cluster = Cluster(context.system)

  override def receive = {
    case Add => println(s"[BACKEND] Member $self was added")
    case MemberUp(member) =>
      if(member.hasRole("frontend")) {
        println(s"[BACKEND] Actor with role 'frontend' is up (${sender()})")
        context.actorSelection(RootActorPath(member.address) / "user" / Frontend.name) ! BackendRegistration
      }
    case default => println(s"[BACKEND] Unknown command $default")
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = cluster.unsubscribe(self)
}
