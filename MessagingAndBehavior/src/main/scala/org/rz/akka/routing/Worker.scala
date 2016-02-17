package org.rz.akka.routing

import akka.actor.{Props, Actor}
import org.rz.akka.routing.Worker.Work

/**
  * Companion object for Worker actor.
  */
object Worker {
  sealed trait WorkerMessage
  case class Work() extends WorkerMessage
  def props = Props[Worker]
}

/**
  * Worker actor.
  */
class Worker extends Actor{

  override def receive = {
    case Work => println(s"[WORKER] I've received a Work message and I'm $self")
    case _ => println(s"[WORKER] Unknown message")
  }

}
