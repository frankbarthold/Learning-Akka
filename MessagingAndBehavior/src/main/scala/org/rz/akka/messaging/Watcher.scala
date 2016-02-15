package org.rz.akka.messaging

import akka.actor.{ActorRef, Actor}

/**
  * Watcher actor for other actors.
  */
class Watcher(counter: ActorRef) extends Actor{

  override def receive = {
    case
    case _ => println("Unknown message")
  }

}
