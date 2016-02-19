package org.rz.akka.routing

import akka.actor.{Props, ActorSystem}
import akka.routing.{RandomGroup, FromConfig}
import org.rz.akka.routing.Worker.Work

/**
  * Runnable object for testing basic router.
  */
object RouterApp extends App{

  val system = ActorSystem("Actor-System")
  val router = system.actorOf(Props[Router], "Router-Actor")

  router ! Worker.Work
  router ! Worker.Work
  router ! Worker.Work
  router ! Worker.Work
  router ! Worker.Work

  Thread.sleep(3000)

  system.terminate()

}

/**
  * Runnable object for testing group routing.
  */
object GroupRouterApp extends App {

  val system = ActorSystem("Actor-System-GroupRouting")

  system.actorOf(Props[Worker], "Worker-Actor-1")
  system.actorOf(Props[Worker], "Worker-Actor-2")
  system.actorOf(Props[Worker], "Worker-Actor-3")
  system.actorOf(Props[Worker], "Worker-Actor-4")
  system.actorOf(Props[Worker], "Worker-Actor-5")

  val paths: List[String] = List(
    "/user/Worker-Actor-1",
    "/user/Worker-Actor-2",
    "/user/Worker-Actor-3",
    "/user/Worker-Actor-4",
    "/user/Worker-Actor-5"
  )

  val router = system.actorOf(Props(new GroupRouter(paths)), "GroupRouter-Actor")

  router ! Work
  router ! Work

  Thread.sleep(3000)

  system.terminate()

}

/**
  * Runnable object for testing random router generation from config file.
  */
object RandomRouterFromConfig extends App {

  val system = ActorSystem("Actor-System-Random-Router-From-Config")
  val routerPool = system.actorOf(FromConfig.props(Props[Worker]), "Random-Router-Pool")

  routerPool ! Work
  routerPool ! Work
  routerPool ! Work
  routerPool ! Work

  Thread.sleep(3000)

  system.terminate()
}

/**
  * Runnable object for testing random router generation programmatically.
  */
object RandomRouterProgrammatically extends App {

  val system = ActorSystem("Actor-System-Random-Router-Programmatically")

  system.actorOf(Props[Worker], "Worker-Actor-1")
  system.actorOf(Props[Worker], "Worker-Actor-2")
  system.actorOf(Props[Worker], "Worker-Actor-3")

  val paths: List[String] = List(
    "/user/Worker-Actor-1",
    "/user/Worker-Actor-2",
    "/user/Worker-Actor-3"
  )

  val routerPool = system.actorOf(RandomGroup(paths).props(), "Random-Router-Programmatically")

  routerPool ! Work
  routerPool ! Work
  routerPool ! Work

  Thread.sleep(3000)

  system.terminate()
}
