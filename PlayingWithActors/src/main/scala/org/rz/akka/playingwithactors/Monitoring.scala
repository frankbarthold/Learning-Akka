package org.rz.akka.playingwithactors

import akka.actor._
import org.rz.akka.playingwithactors.MChild.Message

object MParent {
  def props(child: ActorRef) = Props(new MParentActor(child))
}

/**
  * Parent actor.
  */
class MParentActor(child: ActorRef) extends Actor {

  override def receive = {
    case Terminated =>
      println("[PARENT] Child has died!")
      context.stop(self)
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    println("[PARENT] PreStart")
    context.watch(child)
  }
}

object MChild {

  sealed trait MChildCommand

  case class Message(message: String) extends MChildCommand

}

class MChildActor extends Actor {

  override def receive = {
    case Message(msg) =>
      println(s"[CHILD] Received message: $msg")
      context.stop(self)
    case _ =>
      println("[CHILD] Unknown command")
  }

}

/**
  * Runnable app.
  */
object MonitoringApp extends App {

  val system = ActorSystem("Monitoring-System")
  val child = system.actorOf(Props[MChildActor], "Child-Actor")
  val parent = system.actorOf(MParent.props(child), "Parent-Actor")

  child ! Message("Hello")
  Thread.sleep(3000)

  system.terminate()

}
