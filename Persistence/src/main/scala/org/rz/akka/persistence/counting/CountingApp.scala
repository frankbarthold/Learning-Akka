package org.rz.akka.persistence.counting

import akka.actor.{Props, ActorSystem}
import org.rz.akka.persistence.counting.Counter.{Decrement, Cmd, Increment}

/**
  * Runnable object for testing persistent actor.
  */
object CountingApp extends App{

  val system = ActorSystem("Actor-System-Persistence")
  val counter = system.actorOf(Props[Counter], "Persistent-Actor-Counter")

  counter ! Cmd(Increment(3))
  counter ! Cmd(Increment(5))
  counter ! Cmd(Decrement(3))
  counter ! "print"

  Thread.sleep(1000)
  system.terminate()

}
