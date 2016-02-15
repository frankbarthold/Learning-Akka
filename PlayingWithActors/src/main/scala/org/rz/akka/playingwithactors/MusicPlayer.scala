package org.rz.akka.playingwithactors

import akka.actor.{ActorSystem, Props, Actor}
import org.rz.akka.playingwithactors.MusicController.{Stop, Play}
import org.rz.akka.playingwithactors.MusicPlayer.{StopMusic, StartMusic}

/**
  * Music player commands.
  */
object MusicPlayer {

  sealed trait MusicPlayer

  case class StartMusic() extends MusicPlayer

  case class StopMusic() extends MusicPlayer

}

/**
  * Music player actor.
  */
class MusicPlayer extends Actor {

  override def receive = {
    case StartMusic =>
      //BAD (breakes encapsulation): val controller = context.actorOf(Props[MusicController], "Music_Controller")
      val controller = context.actorOf(MusicController.props, "Music_Controller")
      controller ! Play
    case StopMusic => println("Please don't stop the music!")
  }

}

/**
  * Music controller commands.
  */
object MusicController {

  sealed trait ControllerCommand

  case class Play() extends ControllerCommand

  case class Stop() extends ControllerCommand

  def props = Props[MusicController]
}

/**
  * Music controller actor.
  */
class MusicController extends Actor {

  override def receive = {
    case Play => println("Playing song...")
    case Stop => println("Stopping song...")
    case _ => println("Unknown command...")
  }

}

/**
  * Main app
  */
object MusicPlayerApp extends App {

  val system = ActorSystem("Music_Playing_System")
  val player = system.actorOf(Props[MusicPlayer], "Music_Player_Actor")

  player ! StartMusic
}

