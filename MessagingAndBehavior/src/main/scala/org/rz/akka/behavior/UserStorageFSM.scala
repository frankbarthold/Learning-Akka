package org.rz.akka.behavior

import akka.actor.{Stash, FSM}

/**
  * Companion object for Finite State Machine based UserStorage actor
  */
object UserStorageFSM {

  // FSM current state
  sealed trait State
  case object Connected extends State
  case object Disconnected extends State

  // FSM current data
  sealed trait Data

  case object EmptyData extends Data

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
  * User storage FSM actor.
  */
class UserStorageFSM extends FSM[UserStorageFSM.State, UserStorageFSM.Data] with Stash  {
  import org.rz.akka.behavior.UserStorageFSM._
  import org.rz.akka.behavior.UserStorageFSM.Connection._

  // Initial state
  startWith(Disconnected, EmptyData)

  // State handling
  when(Disconnected){
    case Event(Connect, _) =>
      println("[USERSTORAGEFSM] Connected to DB")
      unstashAll() // Retrieve all stashed Operation messages
      goto(Connected) using EmptyData

    case Event(Operation, _) =>
      stash() // Send operation messages to stashed messages mailbox.
      stay using EmptyData
  }

  when(Connected){
    case Event(Disconnect, _) =>
      println("[USERSTORAGEFSM] Disconnected to DB")
      goto(Disconnected) using EmptyData

    case Event(Operation(op,user), _) =>
      println(s"[USERSTORAGEFSM] Requested operation $op for user $user")
      stay using EmptyData
  }

  whenUnhandled {
    case Event(_, _) => println("[USERSTORAGEFSM] Received an event that was unhandled")
      stay using EmptyData
  }

  // Initialize
  initialize()

}
