package org.rz.akka.cluster.singleton

import akka.actor.{ActorLogging, ActorRef, Props}
import akka.persistence.{PersistentActor, SnapshotOffer}

/**
  * Companion object for Master actor
  */
object Master {

  sealed trait MasterCommand

  case class RegisterWorker(worker: ActorRef) extends MasterCommand

  case class RequestWork(requester: ActorRef) extends MasterCommand

  case class Work(requester: ActorRef, operation: String) extends MasterCommand

  sealed trait MasterEvent

  case class WorkerAdded(worker: ActorRef) extends MasterEvent

  case class WorkRequested(work: Work) extends MasterEvent

  case class UpdateWorks(works: List[Work]) extends MasterEvent

  case class MasterState(workers: Set[ActorRef], works: List[Work])

  case object NoWork

  def props: Props = Props(new Master)

}

/**
  * Master actor.
  */
class Master extends PersistentActor with ActorLogging {

  import org.rz.akka.cluster.singleton.Master._

  // Internal state: workers & works.
  var workers: Set[ActorRef] = Set.empty
  var works: List[Work] = List.empty

  // Persistence identifier.
  override def persistenceId: String = self.path.parent.name + "-" + self.path.name

  /**
    * Recovering
    */
  override def receiveRecover: Receive = {
    case evt: MasterEvent =>
      updateState(evt)
    case SnapshotOffer(_, snapshot: MasterState) =>
      workers = snapshot.workers
      works = snapshot.works
  }

  /**
    * Processes the commands received by the actor.
    */
  override def receiveCommand: Receive = {
    case RegisterWorker(worker) =>
      persist(WorkerAdded(worker)) {
        evt => updateState(evt)
      }

    case RequestWork(requester) if works.isEmpty =>
      sender() ! NoWork

    case RequestWork(requester) if workers.contains(requester) && works.nonEmpty =>
      sender() ! works.head
      persist(UpdateWorks(works.tail)) {
        evt => updateState(evt)
      }

    case work: Work =>
      persist(WorkRequested(work)) {
        evt => updateState(evt)
      }
  }

  // Update the internal state
  def updateState(evt: MasterEvent) = evt match {
    case WorkerAdded(w) =>
      workers = workers + w

    case WorkRequested(w) =>
      works = works :+ w

    case UpdateWorks(ws) =>
      works = ws
    case default => println(s"[MASTER] Unknown state message $default received")
  }

}
