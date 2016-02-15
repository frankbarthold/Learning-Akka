package org.rz.akka.messaging

import akka.actor._

/**
  * Companion object for watcher actor.
  */
object Watcher{
  val actorName = "Watcher-Actor"
  def props(counter: ActorRef) = Props(new Watcher(counter))
}

/**
  * Watcher actor for other actors.
  */
class Watcher(counter: ActorRef) extends Actor{
  import org.rz.akka.messaging.Counter

  val select = context.actorSelection("/user/" + Counter.actorName)
  select ! Identify(None)

  override def receive = {
    case ActorIdentity(_, Some(ref)) => println(s"[WATCHER] Actor identity is $ref")
    case ActorIdentity(_, None) => println("[WATCHER] Actor identity has not a defined reference")
    case _ => println("Unknown message")
  }

}
