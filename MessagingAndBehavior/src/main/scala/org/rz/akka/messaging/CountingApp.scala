package org.rz.akka.messaging

import akka.actor.{PoisonPill, Props, ActorSystem}

/**
  * Runnable object for counting (references) example.
  */
object CountingApp extends App{

  val system = ActorSystem("Actor-System")
  val counter = system.actorOf(Props[Counter], Counter.actorName)
  println(s"ActorRef for counter: $counter")

  val counterSelection = system.actorSelection(Counter.actorName)
  println(s"ActorSelection for counter: $counterSelection")

  counter ! PoisonPill //This kills the actor
  Thread.sleep(3000)

  val counter2 = system.actorOf(Props[Counter], Counter.actorName)
  println(s"ActorRef for counter2: $counter2")

  val counterSelection2 = system.actorSelection(Counter.actorName)
  println(s"ActorSelection for counter2: $counterSelection2")

  system.terminate()
}

/**
  * Runnable object for Watcher example.
  */
object WatchingApp extends App{

  val system = ActorSystem("Actor-System")

  val counter = system.actorOf(Props[Counter], Counter.actorName)
  val watcher = system.actorOf(Watcher.props(counter), Watcher.actorName)
  println(s"ActorRef for counter: $counter")

  system.terminate()
}