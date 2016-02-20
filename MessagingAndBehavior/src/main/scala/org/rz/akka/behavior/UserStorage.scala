package org.rz.akka.behavior

import akka.actor.{Stash, Actor}
import org.rz.akka.behavior.UserStorage.Connection.{Disconnect, Connect}
import org.rz.akka.behavior.UserStorage.Operation

/**
  * Companion object containing DB operations.
  */
object UserStorage {

  // Base trait for CRUD operations.
  sealed trait DBOperation

  // Base trait for connection operations to database
  sealed trait Connection

  /**
    * Object containing CRUD objects.
    */
  object DBOperation {

    case object Create extends DBOperation

    case object Read extends DBOperation

    case object Update extends DBOperation

    case object Delete extends DBOperation

  }

  /**
    * Object containing connection objects.
    */
  object Connection {

    case object Connect extends Connection

    case object Disconnect extends Connection

  }

  /**
    * Operation performing class.
    *
    * @param operation The operation class to be executed.
    * @param user      An optional user
    */
  case class Operation(operation: DBOperation, user: Option[User])

}

/**
  * User storage actor.
  */
class UserStorage extends Actor with Stash {

  // Actor connected state
  def connected: Actor.Receive = {
    case Disconnect =>
      println("[USERSTORAGE] Disconnected to DB")
      context.unbecome()
    case Operation(operation, user) =>
      println(s"[USERSTORAGE] Requested operation $operation for user $user")
    case _ => println("Unknown message")
  }

  // Actor disconnected state
  def disconnected: Actor.Receive = {
    case Connect =>
      println("[USERSTORAGE] Connected to DB")
      unstashAll() // Retrieve all stashed Operation messages
      context.become(connected)
    case operation: Operation =>
      stash() // Send operation messages to stashed messages mailbox.
    case _ => println("Unknown message")
  }

  // By default the Actor will be in disconnected state
  override def receive = disconnected
}
