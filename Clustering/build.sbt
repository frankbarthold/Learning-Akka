name := "Clustering"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.2",
  "com.typesafe.akka" %% "akka-remote" % "2.4.2",
  "com.typesafe.akka" %% "akka-cluster" % "2.4.2",
  "com.typesafe.akka" %% "akka-cluster-metrics" % "2.4.2",
  "com.typesafe.akka" %% "akka-persistence" % "2.4.2",
  "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.2",
  "org.iq80.leveldb" % "leveldb" % "0.7",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"
)