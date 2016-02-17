package org.rz.akka.routing

import akka.actor.{ActorRef, Actor}
import org.rz.akka.routing.Worker.Work

/**
  * Router actor.
  */
class Router extends Actor {

  var children: List[ActorRef] = _

  override def preStart() = {
    children = List.fill(5)(
      context.actorOf(Worker.props)
    )
  }

  override def receive() = {
    case msg: Work =>
      val destination = util.Random.nextInt(children.size)
      println(s"[ROUTER] Forwarding message to children #$destination")
      children(destination).forward(msg)
    case _ => println("[ROUTER] Unknown message")
  }

}
