package org.rz.akka.cluster.sharding

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.sharding.ClusterSharding
import org.rz.akka.cluster.sharding.Counter.CounterMessage

import scala.concurrent.duration.Duration

/**
  * Companion object for Frontend actor
  */
object Frontend {

  def props: Props = Props[Frontend]

  sealed trait Command
  case object Increment extends Command
  case object Decrement extends Command
  case object Get extends Command

  case class Tick(cmd: Command)

}

/**
  * Frontend actor that handles messages and forwards them
  * to the singleton master actor.
  */
class Frontend extends Actor {
  import org.rz.akka.cluster.sharding.Frontend._

  import scala.concurrent.ExecutionContext.Implicits.global

  // Reference to the sharding region named in Counter.
  val counterShardingRegion: ActorRef = ClusterSharding(context.system).shardRegion(Counter.shardName)

  // Schedule message sending
  context.system.scheduler.schedule(Duration(3, TimeUnit.SECONDS), Duration(3, TimeUnit.SECONDS), self, Tick(Frontend.Increment))
  context.system.scheduler.schedule(Duration(6, TimeUnit.SECONDS), Duration(3, TimeUnit.SECONDS), self, Tick(Frontend.Decrement))
  context.system.scheduler.schedule(Duration(10, TimeUnit.SECONDS), Duration(3, TimeUnit.SECONDS), self, Tick(Frontend.Get))

  override def receive = {
    case Tick(Frontend.Increment) =>
      println("[FRONTEND] Sending INCREMENT to shard region")
      counterShardingRegion ! CounterMessage(getId, Counter.Increment)

    case Tick(Frontend.Decrement) =>
      println("[FRONTEND] Sending DECREMENT to shard region")
      counterShardingRegion ! CounterMessage(getId, Counter.Decrement)

    case Tick(Frontend.Get) =>
      println("[FRONTEND] Sending DECREMENT to shard region")
      counterShardingRegion ! CounterMessage(getId, Counter.Get)

    case default => println(s"[FRONTEND] Unknown message $default")
  }

  def getId = util.Random.nextInt(5)
}