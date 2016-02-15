package org.rz.akka.playingwithactors

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Actor, Props, ActorRef}
import akka.util.Timeout
import akka.pattern.ask
import org.rz.akka.playingwithactors.Checker.{WhiteUser, BlackUser}
import Checker.{BlackUser, WhiteUser}
import Recorder.{AddUser, CheckUser, NewUser}

/**
  * Recorder messages.
  */
object Recorder {

  sealed trait RecorderMessage

  case class CheckUser(user: String) extends RecorderMessage

  case class AddUser(user: String) extends RecorderMessage

  case class NewUser(user: String) extends RecorderMessage

  def props(checker: ActorRef, storage: ActorRef) = Props(new Recorder(checker, storage))
}


/**
  * Recorder actor
  */
class Recorder(checker: ActorRef, storage: ActorRef) extends Actor {

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  def receive = {
    case NewUser(user) =>
      checker ? CheckUser(user) map {
        case BlackUser =>
          println(s"The user $user is blacklisted")
        case WhiteUser =>
          println(s"The user $user is not blacklisted")
          storage ! AddUser
      }
    case _ => println("Unknown message")
  }

}

/**
  * Checker messages
  */
object Checker {

  sealed trait CheckerMessage

  case class WhiteUser() extends CheckerMessage

  case class BlackUser() extends CheckerMessage

  def props = Props[Checker]
}

/**
  * Checker actor.
  */
class Checker extends Actor {

  def blacklist = List {
    "Pepe"
  }

  def receive = {
    case CheckUser(user) if blacklist.contains(user) =>
      println(s"$user is a blacklisted user")
      sender() ! BlackUser()
    case CheckUser(user) =>
      println(s"$user is not a blacklisted user")
      sender() ! WhiteUser()
    case _ => println("Unknown message")
  }

}

/**
  * Storage companinon object.
  */
object Storage {
  def props = Props[Storage]
}

/**
  * Storage actor.
  */
class Storage extends Actor {

  // List of stored users.
  var users = List.empty[String]

  def receive = {
    case AddUser(user) =>
      println(s"Adding a new user $user")
      users = user :: users
    case _ => println("Unknown message")
  }

}

/**
  * Run app.
  */
object UserRegistration extends App {

  val system = ActorSystem("User_Registration")
  val storage = system.actorOf(Props[Storage], "Storage_Actor")
  val checker = system.actorOf(Props[Checker], "Checker_Actor")
  val recorder = system.actorOf(Recorder.props(checker, storage), "Recorder_Actor")

  recorder ! NewUser("Pepe")
  recorder ! NewUser("Pepa")

  Thread.sleep(100)

  system.terminate()
}



