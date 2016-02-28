package org.rz.akka.cluster.loadbalanced

/**
  * Runnable app for testing a load balanced cluster.
  */
object LoadBalancedClusterApp extends App{

  LoadBalancingBackend.initiate(2551)
  LoadBalancingBackend.initiate(2552)
  LoadBalancingFrontend.initiate()

}
