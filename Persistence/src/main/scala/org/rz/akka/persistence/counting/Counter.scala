package org.rz.akka.persistence.counting

import akka.actor.ActorLogging
import akka.persistence.{SnapshotOffer, PersistentActor}
import org.rz.akka.persistence.counting.Counter._

/**
  * Companion object for counter persistent actor.
  */
object Counter {

  // Operations for the counter
  sealed trait Operation{
    val count: Int
  }
  case class Increment(override val count: Int) extends Operation
  case class Decrement(override val count: Int) extends Operation

  // Command & Event persistency
  case class Cmd(op: Operation)
  case class Evt(op: Operation)

  // Actor internal state.
  case class State(count: Int)
}

/**
  * Counter persistent actor.
  */
class Counter extends PersistentActor with ActorLogging {

  // Unique id for actor's persistence
  override def persistenceId: String = "Counter-Persistent-Actor"

  // Internal state def
  var state: State = State(0)

  // Update the internal state
  def updateState(evt: Evt) = evt match {
    case Evt(Increment(count)) =>
      state = State(count + state.count)
    case Evt(Decrement(count)) => state = State(count - state.count)
    case _ => println("[COUNTER] Unknown state message received")
  }

  // Recover the actor state from journal
  override def receiveRecover: Receive = {
    case evt: Evt =>
      println(s"[COUNTER] Received recover event with counter $evt")
      updateState(evt)
    case SnapshotOffer(_, snapshot: State) =>
      println(s"[COUNTER] Received recover event from snapshot with counter $snapshot")
      state = snapshot
  }

  // Checks if command can be applied on current state
  override def receiveCommand: Receive = {
    case cmd @ Cmd(op) =>
      println(s"[COUNTER] Received command with counter $op")
      persist(Evt(op)) {
        evt => updateState(evt)
      }
    case "print" =>
      println(s"[COUNTER] Current state is $state")
  }

}
