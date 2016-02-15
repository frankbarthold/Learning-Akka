package org.rz.akka.messaging

import akka.actor.{PoisonPill, Props, ActorSystem}

/**
  * Runnable object
  */
object CountingApp extends App{

  val system = ActorSystem("Actor-System")
  val actorName: String = "Counter-Actor"
  val counter = system.actorOf(Props[Counter], actorName)
  println(s"ActorRef for counter: $counter")

  val counterSelection = system.actorSelection(actorName)
  println(s"ActorSelection for counter: $counterSelection")

  counter ! PoisonPill
  Thread.sleep(3000)

  val counter2 = system.actorOf(Props[Counter], actorName)
  println(s"ActorRef for counter2: $counter2")

  val counterSelection2 = system.actorSelection(actorName)
  println(s"ActorSelection for counter2: $counterSelection2")

  system.terminate()
}
