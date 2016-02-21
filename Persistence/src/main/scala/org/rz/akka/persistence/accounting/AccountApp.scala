package org.rz.akka.persistence.accounting

import akka.NotUsed
import akka.actor.{Props, ActorSystem}
import akka.persistence.query.{EventEnvelope, PersistenceQuery}
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
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

/**
  * Runnable app for testing querying over FSM-based persistent actor events.
  */
object EventReader extends App{

  val system = ActorSystem("Actor-System-FSM-Persistence")
  implicit val mat = ActorMaterializer()(system)

  val queries = PersistenceQuery(system).readJournalFor[LeveldbReadJournal](
    LeveldbReadJournal.Identifier
  )

  val events: Source[EventEnvelope, NotUsed] =
    queries.eventsByPersistenceId("Counter-Persistent-Actor")

  events.runForeach(evt => println(s"Observed event $evt"))

  Thread.sleep(1000)
  system.terminate()

}
