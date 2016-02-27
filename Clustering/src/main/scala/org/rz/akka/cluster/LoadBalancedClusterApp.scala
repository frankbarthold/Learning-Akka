package org.rz.akka.cluster

import org.rz.akka.cluster.LoadBalancingBackend.Add

/**
  * Runnable app for testing a load balanced cluster.
  */
object LoadBalancedClusterApp extends App{

  LoadBalancingBackend.initiate(2551)
  LoadBalancingBackend.initiate(2552)

  LoadBalancingFrontend.initiate()
  LoadBalancingFrontend.getFrontend ! Add

}
