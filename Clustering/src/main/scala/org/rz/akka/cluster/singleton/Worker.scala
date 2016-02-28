package org.rz.akka.cluster.singleton

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, Props}
import akka.cluster.singleton.{ClusterSingletonProxy, ClusterSingletonProxySettings}
import org.rz.akka.cluster.singleton.Master.{RegisterWorker, RequestWork, Work}

import scala.concurrent.duration.Duration

/**
  * Companion object for Worker actor.
  */
object Worker {

  def props: Props = Props(new Worker)

}

/**
  * Worker actor that performs the final computation (just printing a message).
  */
class Worker extends Actor {

  import scala.concurrent.ExecutionContext.Implicits.global

  // Reference to the master proxy that will resolve the singleton worker instance of master actor.
  val masterProxy = context.actorOf(ClusterSingletonProxy.props(
    singletonManagerPath = "/user/Master",
    settings = ClusterSingletonProxySettings(context.system).withRole(None)
  ), name = "masterProxy")

  // Schedule periodic register & work request.
  context.system.scheduler.schedule(Duration(0, TimeUnit.SECONDS), Duration(30, TimeUnit.SECONDS), masterProxy, RegisterWorker(self))
  context.system.scheduler.schedule(Duration(3, TimeUnit.SECONDS), Duration(3, TimeUnit.SECONDS), masterProxy, RequestWork(self))

  override def receive = {
    case Work(requester, op) =>
      println(s"[WORKER] I'm ${self.path} and I've received an operation $op from ${requester.path}")
    case default => println(s"[WORKER] Unknown message $default")
  }

}
