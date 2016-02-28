package org.rz.akka.cluster.singleton

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, Props}
import akka.cluster.singleton.{ClusterSingletonProxy, ClusterSingletonProxySettings}
import org.rz.akka.cluster.singleton.Frontend.Tick

import scala.concurrent.duration.Duration

/**
  * Companion object for Frontend actor
  */
object Frontend {

  def props: Props = Props(new Frontend)

  case object Tick

}

class Frontend extends Actor {

  import scala.concurrent.ExecutionContext.Implicits.global

  // Reference to the master proxy that will resolve the singleton worker instance of master actor.
  val masterProxy = context.actorOf(ClusterSingletonProxy.props(
    singletonManagerPath = "/user/Master",
    settings = ClusterSingletonProxySettings(context.system).withRole(None)
  ), name = "masterProxy")

  // Schedule periodic tick command.
  context.system.scheduler.schedule(Duration(0, TimeUnit.SECONDS), Duration(3, TimeUnit.SECONDS), self, Tick)

  override def receive = {
    case Tick =>
      masterProxy ! Master.Work(self, "add")

    case default => println(s"[FRONTEND] Unknown message $default")
  }
}