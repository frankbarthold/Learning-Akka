package org.rz.akka.behavior

/**
  * User entity.
  *
  * @param name  User name
  * @param email User email
  */
case class User(name: String, email: String) {
  override def toString = s"($name <$email>)"
}



