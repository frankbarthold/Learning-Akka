package org.rz.akka.streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Minimal Akka flow for stream processing.
  */
object BasicFlow extends App {

  // Akka streaming will provide these values.
  implicit val actorSystem = ActorSystem()
  implicit val flowMaterializer = ActorMaterializer()

  val input = Source(1 to 100) // Source
  val normalize = Flow[Int].map(_ * 2) // Intermediate processing
  val output = Sink.foreach[Int](println) // Sink

  // Create the stream
  val future = input.via(normalize).runWith(output)

  // Wait for the flow to end and then terminate the system.
  Await.result(future, Duration.Inf)
  actorSystem.terminate()

}
