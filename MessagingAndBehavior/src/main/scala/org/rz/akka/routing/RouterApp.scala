package org.rz.akka.routing

import akka.actor.{Props, ActorSystem}
import org.rz.akka.routing.Worker.Work

/**
  * Runnable object.
  */
object RouterApp extends App{

  val system = ActorSystem("Actor-System")
  val router = system.actorOf(Props[Router], "Router-Actor")

  router ! Worker.Work
  router ! Worker.Work
  router ! Worker.Work
  router ! Worker.Work
  router ! Worker.Work

  Thread.sleep(3000)

  system.terminate()

}
