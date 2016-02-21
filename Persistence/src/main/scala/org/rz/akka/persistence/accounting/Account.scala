package org.rz.akka.persistence.accounting

import akka.actor.ActorLogging
import akka.persistence.fsm.PersistentFSM
import akka.persistence.fsm.PersistentFSM.FSMState
import org.rz.akka.persistence.accounting.Account._

import scala.reflect._

/**
  * Companion object for account actor.
  */
object Account {

  // State
  sealed trait State extends FSMState
  case object Empty extends State {
    override def identifier = "Empty-State"
  }
  case object Active extends State {
    override def identifier = "Active-State"
  }

  // Data
  sealed trait Data {
    val amount: Float
  }
  case object ZeroBalance extends Data {
    override val amount: Float = 0.0f
  }
  case class Balance(override val amount: Float) extends Data

  // Events
  sealed trait DomainEvent
  case class AcceptedTransaction(amount: Float, `type`: TransactionType) extends DomainEvent
  case class RejectedTransaction(amount: Float, `type`: TransactionType, reason: String) extends DomainEvent

  sealed trait TransactionType
  case object Debit extends TransactionType
  case object Credit extends TransactionType

  // Commands
  case class Operation(amount: Float, `type`: TransactionType)

}

/**
  * Account management actor.
  */
class Account extends PersistentFSM[State, Data, DomainEvent] with ActorLogging{
  import Account._

  override implicit def domainEventClassTag: ClassTag[DomainEvent] = classTag[DomainEvent]

  override def persistenceId: String = "Account-Actor-Persistence-ID"

  override def applyEvent(domainEvent: DomainEvent, currentData: Data): Data = {
    domainEvent match {
      case AcceptedTransaction(amount, Credit) =>
        val newBalance = currentData.amount + amount
        println(s"[ACCOUNT] The credit transaction was accepted (new balance is $newBalance)")
        Balance(newBalance)

      case AcceptedTransaction(amount, Debit) =>
        val newBalance = currentData.amount - amount
        println(s"[ACCOUNT] The debit transaction was accepted (new balance is $newBalance)")
        if(newBalance > 0)
          Balance(newBalance)
        else
          ZeroBalance

      case RejectedTransaction(_, _, reason) =>
        println(s"[ACCOUNT] The transaction was rejected due to $reason")
        currentData
    }
  }

  // Initial state for FSM
  startWith(Empty, ZeroBalance)

  // Cases
  when(Empty){
    case Event(Operation(amount, Credit), _) =>
      println("[ACCOUNT] Received Credit operation with state Empty")
      goto(Active) applying AcceptedTransaction(amount, Credit)

    case Event(Operation(amount, Debit), _) =>
      println("[ACCOUNT] Received Debit operation with state Empty")
      stay applying RejectedTransaction(amount, Debit, s"Insufficient credit (${stateData.amount})")
  }

  when(Active){
    case Event(Operation(amount, Credit), _) =>
      println("[ACCOUNT] Received Credit operation with state Active")
      stay applying AcceptedTransaction(amount, Credit)

    case Event(Operation(amount, Debit), balance) =>
      println("[ACCOUNT] Received Debit operation with state Active")
      val newBalance = balance.amount - amount
      newBalance match {
        case 0 =>
          goto(Empty) applying AcceptedTransaction(amount, Debit)
        case _ if newBalance > 0 =>
          stay applying AcceptedTransaction(amount, Debit)
        case _ =>
          stay applying RejectedTransaction(amount, Debit, s"Insufficient credit (${balance.amount})")
      }
  }

}
