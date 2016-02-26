package org.rz.akka.remote

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import org.rz.akka.remote.Worker.Message

/**
  * Runnable application for testing remote actor.
  */
object WorkerApp extends App{

  val config = ConfigFactory.load().getConfig("Remote-Exercise")
  val system = ActorSystem("Remote-System", config)

  val actor = system.actorOf(Props[Worker], "Remote-Worker-Actor")
  println(s"[INFO] Actor path of the newly created actor is ${actor.path}")

  //Thread.sleep(1000)
  //system.terminate()

}

/**
  * Runnable application for testing remote actor lookup.
  */
object WorkerLookupApp extends App{

  val config = ConfigFactory.load().getConfig("Remote-Exercise-Lookup")
  val system = ActorSystem("Remote-System-Lookup", config)

  val actor = system.actorSelection("akka.tcp://Remote-System@127.0.0.1:2552/user/Remote-Worker-Actor")
  actor ! Message(s"Hello from ${system.name}")

  //Thread.sleep(5000)
  //system.terminate()

}

/**
  * Runnable application for testing remote actor creation.
  */
object WorkerRemoteCreationApp extends App{

  val config = ConfigFactory.load().getConfig("Remote-Exercise-Creation")
  val system = ActorSystem("Remote-Creation-System", config)

  val actor = system.actorOf(Props[Worker], "Remotely-Created-Worker-Actor")
  println(s"[INFO] Actor path of the newly created actor is ${actor.path}")

  actor ! Message(s"Hello from ${system.name}")

//  Thread.sleep(5000)
//  system.terminate()
}