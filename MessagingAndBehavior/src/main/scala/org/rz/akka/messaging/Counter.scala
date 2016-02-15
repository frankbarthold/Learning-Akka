package org.rz.akka.messaging

import akka.actor.Actor

/**
  * Object companion for Counter actor.
  */
object Counter {
  sealed trait CounterMessage
  case class Incr(x: Int) extends CounterMessage
  case class Decr(x: Int) extends CounterMessage
}

/**
  * Counter actor.
  */
class Counter extends Actor{
  import org.rz.akka.messaging.Counter._

  var count = 0

  override def receive = {
    case Incr(x) => count += x
    case Decr(x) => count -= x
    case _ => println("Unknown message")
  }

}


