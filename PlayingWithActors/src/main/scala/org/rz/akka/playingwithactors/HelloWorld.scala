package org.rz.akka.playingwithactors

import akka.actor.{Props, ActorSystem, Actor}
import akka.actor.Actor.Receive

/**
  * Case class that defines the messages.
  *
  * @param who
  */
case class WhoToGreet(who: String)

/**
  * Akka actor that performs a greeting.
  */
class Greeter extends Actor {
  override def receive = {
    case WhoToGreet(who) => println(s"Hello $who")
  }
}

/**
  * Main object.
  */
object HelloWorldAkka extends App {

  // Create actor system
  val system = ActorSystem("Hello_World")

  // Create the greeter actor
  val greeter = system.actorOf(Props[Greeter], "Greeter")

  // Send the actor, the WhoToGreet message
  greeter ! WhoToGreet("Akka")

}
