package org.rz.akka.behavior

import akka.actor.{Props, ActorSystem}
import org.rz.akka.behavior.UserStorage.Connection.{Connect, Disconnect}
import org.rz.akka.behavior.UserStorage.DBOperation.Create
import org.rz.akka.behavior.UserStorage.Operation

/**
  * Runnable object for testing behavior management.
  */
object UserStorageApp extends App {

  val system = ActorSystem("Actor-System-Behavior")
  val userStorage = system.actorOf(Props[UserStorage], "User-Storage-Actor")

  //  userStorage ! Connect
  //  userStorage ! Operation(Create, Some(User("Pepe", "pepe@nowhere.com")))
  //  userStorage ! Disconnect

  // This operation message will be stashed for beign processed when the actor
  // reached the Connected state.
  userStorage ! Operation(Create, Some(User("Pepe", "pepe@nowhere.com")))
  userStorage ! Connect
  userStorage ! Disconnect

  Thread.sleep(1000)
  system.terminate()

}

/**
  * Runnable object for testing behavior management with Finite State Machine.
  */
object UserStorageFSMApp extends App {

  val system = ActorSystem("Actor-System-Behavior-FSM")
  val userStorage = system.actorOf(Props[UserStorageFSM], "User-Storage-Actor-FSM")

//    userStorage ! Connect
//    userStorage ! Operation(Create, Some(User("Pepe", "pepe@nowhere.com")))
//    userStorage ! Disconnect

  // This operation message will be stashed for beign processed when the actor
  // reached the Connected stat
   userStorage ! Operation(Create, Some(User("Pepe", "pepe@nowhere.com")))
   userStorage ! Connect
   userStorage ! Disconnect

  Thread.sleep(1000)
  system.terminate()

}
