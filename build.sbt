name := "akka-actor-model-iot-workflow"

version := "0.1"

scalaVersion := "2.13.6"

val AkkaVersion = "2.6.15"
val AkkaHttpVersion = "10.2.4"

libraryDependencies ++= Seq(
  /** https://mvnrepository.com/artifact/com.typesafe.akka/akka-actor */
  "com.typesafe.akka" %% "akka-actor" % AkkaVersion,

  /** https://mvnrepository.com/artifact/com.typesafe.akka/akka-testkit */
  "com.typesafe.akka" %% "akka-testkit" % AkkaVersion,

  /** https://mvnrepository.com/artifact/org.scalatest/scalatest */
  "org.scalatest" %% "scalatest" % "3.2.9",


  // for JSON in Scala
  "io.spray" %% "spray-json" % "1.3.6",
  // Logging
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.5",
  // #deps
  "org.testcontainers" % "elasticsearch" % "1.16.0",
  "org.testcontainers" % "kafka" % "1.16.0",

  "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-serialization-jackson" % AkkaVersion,

  "com.github.scullxbones" %% "akka-persistence-mongo-scala" % "3.0.6",

  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.2.9" % Test,

  "com.typesafe.akka" %% "akka-persistence-cassandra" % "1.0.5",
  "com.typesafe.akka" %% "akka-persistence" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence-query" % AkkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % AkkaVersion
)