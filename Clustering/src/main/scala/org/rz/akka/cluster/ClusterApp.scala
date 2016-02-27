package org.rz.akka.cluster

import org.rz.akka.cluster.Backend.Add

/**
  * Runnable application for testing Akka cluster.
  */
object ClusterApp extends App{

  Frontend.initiate()

  Backend.initiate(2552)

  Backend.initiate(2553)

  Backend.initiate(2554)

  Thread.sleep(10000)

  Frontend.getFrontend ! Add

}
