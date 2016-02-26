package org.rz.akka.remote

import akka.actor.{Actor, ActorLogging}
import org.rz.akka.remote.Worker.Message

/**
  * Companion object for Worker actor
  */
object Worker{

  sealed trait WorkerMessage{
    val msg: String
  }
  case class Message(override val msg: String) extends WorkerMessage

}

/**
  * Worker actor
  */
class Worker extends Actor with ActorLogging{

  override def receive = {
    case msg: Message => println(s"[WORKER] Message $msg and my ActorRef is $self")
    case _ => println("Unknown message")
  }

}
