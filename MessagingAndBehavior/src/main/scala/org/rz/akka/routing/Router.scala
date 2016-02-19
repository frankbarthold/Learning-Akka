package org.rz.akka.routing

import akka.actor.{ActorRef, Actor}
import org.rz.akka.routing.Worker.Work

/**
  * Basic router actor.
  */
class Router extends Actor {

  var children: List[ActorRef] = _

  override def preStart() = {
    children = List.fill(5)(
      context.actorOf(Worker.props)
    )
  }

  override def receive = {
    case Work =>
      val destination = util.Random.nextInt(children.size)
      println(s"[ROUTER] Forwarding message to children #$destination")
      children(destination).forward(Work)
    case _ => println("[ROUTER] Unknown message")
  }

}

/**
  * Group router actor.
  * A group router is a router that does not creates the destination actors that it will serve.
  *
  * @param destinations The list of ActorPaths where this router will forward messages to.
  */
class GroupRouter(destinations: List[String]) extends Actor {

  override def receive = {
    case Work =>
      val chosen = util.Random.nextInt(destinations.size)
      println(s"[ROUTER] Forwarding message to children #$chosen")
      context.actorSelection(destinations(chosen)).forward(Work)
    case _ => println("[ROUTER] Unknown message")
  }

}
