package org.rz.akka.cluster.sharding

import akka.actor.{ActorLogging, Props}
import akka.cluster.sharding.ShardRegion
import akka.persistence.PersistentActor
import org.rz.akka.cluster.sharding.Counter._

/**
  * Companion object for Counter actor.
  */
object Counter {

  sealed trait Command
  case object Increment extends Command
  case object Decrement extends Command
  case object Get extends Command
  case object Stop extends Command

  sealed trait Event
  case class CounterChanged(delta: Int) extends Event

  // Message definition for communication with Counter actior from outside the shard.
  case class CounterMessage(id: Int, cmd: Command)

  // Defines how to get the identifier of the actor on the shard.
  val idExtractor: ShardRegion.ExtractEntityId = {
    case CounterMessage(id, cmd) => (id.toString, cmd)
  }

  // Defines how to get the identifier of the shard itself.
  val shardResolver: ShardRegion.ExtractShardId = {
    case CounterMessage(id, cmd) => (id % 10).toString
  }

  val shardName: String = "Counter"

  def props: Props = Props[Counter]

}

/**
  * Counter actor.
  */
class Counter extends PersistentActor with ActorLogging {

  var count: Int = 0

  def updateState(evt: CounterChanged): Unit = evt match {
    case CounterChanged(delta) =>
      count += delta
  }

  override def receiveRecover: Receive = {
    case evt: CounterChanged => updateState(evt)
  }

  override def receiveCommand: Receive = {
    case Increment =>
      println("[COUNTER] Received INCREMENT command")
      persist(CounterChanged(+1)) {
        evt => updateState(evt)
      }
    case Decrement =>
      println("[COUNTER] Received DECREMENT command")
      persist(CounterChanged(-1)) {
        evt => updateState(evt)
      }
    case Get =>
      println(s"[COUNTER] Received GET command (count=$count)")
      sender() ! count

    case Stop =>
      context.stop(self)
  }

  override def persistenceId: String = s"${self.path.parent} - ${self.path}"

}
