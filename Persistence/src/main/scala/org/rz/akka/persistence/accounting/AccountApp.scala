package org.rz.akka.persistence.accounting

import akka.actor.{Props, ActorSystem}
import org.rz.akka.persistence.accounting.Account.{Credit, Debit, Operation}

/**
  * Runnable app for testing FSM-based persistent actors.
  */
object AccountApp extends App {

  val system = ActorSystem("Actor-System-FSM-Persistence")
  val account = system.actorOf(Props[Account], "Persistent-FSM-Account-Actor")

  account ! Operation(5, Debit)
  account ! Operation(10, Credit)
  account ! Operation(5, Debit)
  account ! Operation(5, Debit)

  Thread.sleep(1000)
  system.terminate()
}
