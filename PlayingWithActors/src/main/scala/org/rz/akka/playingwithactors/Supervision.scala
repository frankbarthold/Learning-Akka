package org.rz.akka.playingwithactors

import akka.actor.SupervisorStrategy.{Restart, Stop, Resume, Escalate}
import akka.actor._
import org.rz.akka.playingwithactors.Child.{StopException, RestartException, ResumeException}
import scala.concurrent.duration._

/**
  * Parent actor companion object.
  */
object Parent {

  sealed trait ParentActorCommands

  case class Resume() extends ParentActorCommands

  case class Stop() extends ParentActorCommands

  case class Restart() extends ParentActorCommands

}

/**
  * Parent actor.
  */
class ParentActor extends Actor {

  var child: ActorRef = _

  override val supervisorStrategy: SupervisorStrategy = {
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = Duration(1, MINUTES)) {
      case ResumeException => Resume
      case StopException => Stop
      case RestartException => Restart
      case _: Exception => Escalate
    }
  }

  override def receive = {
    case Resume => child ! Resume
    case Stop => child ! Stop
    case Restart => child ! Restart
    case _ => println("Unknown command")
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    println("[PARENT] PreStart")
    child = context.actorOf(Child.props, "Child-Actor")
  }
}

/**
  * Child actor companion object.
  */
object Child {

  case object ResumeException extends Exception

  case object StopException extends Exception

  case object RestartException extends Exception

  def props = Props[ChildActor]
}

class ChildActor extends Actor {

  override def receive = {
    case Resume => throw ResumeException
    case Stop => throw StopException
    case Restart => throw RestartException
    case _ =>
      println("[CHILD] Running Normally")
      Escalate
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    println("[CHILD] Pre-Start")
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    println("[CHILD] Post-Stop")
  }

  @throws[Exception](classOf[Exception])
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println("[CHILD] Pre-Restart")
    super.preRestart(reason, message)
  }

  @throws[Exception](classOf[Exception])
  override def postRestart(reason: Throwable): Unit = {
    println("[CHILD] Post-Restart")
    super.postRestart(reason)
  }

}

/**
  * Runnable app.
  */
object SupervisionApp extends App {

  val system = ActorSystem("Supervision-System")
  val parent = system.actorOf(Props[ParentActor], "Parent-Actor")


  //  parent ! Resume
  //  Thread.sleep(3000)

  //  parent ! Restart
  //  Thread.sleep(3000)

  parent ! Stop
  Thread.sleep(3000)

  system.terminate()

}
