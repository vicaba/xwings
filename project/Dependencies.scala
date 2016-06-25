import sbt._

object Dependencies {

  val akkaVersion = "2.4.4"

  val playVersion = "2.5.4"

  lazy val akkaRemote = "com.typesafe.akka" %% "akka-remote" % akkaVersion

  lazy val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion

  lazy val akkaStreams = "com.typesafe.akka" %% "akka-stream" % akkaVersion

  lazy val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion

  lazy val playJson = "com.typesafe.play" %% "play-json" % playVersion

  lazy val playReactiveMongo = "org.reactivemongo" %% "play2-reactivemongo" % "0.11.13"

  lazy val playWS = "com.typesafe.play" %% "play-ws" % playVersion

  lazy val playCache = "com.typesafe.play" %% "play-cache" % playVersion

  lazy val typesafeConfig = "com.typesafe" % "config" % "1.3.0"

  lazy val slf4j = "org.slf4j" % "slf4j-simple" % "1.7.21"

  lazy val scalaReflect = "org.scala-lang" % "scala-reflect" % "2.11.7"

  lazy val neo4jCore = "org.neo4j" % "neo4j-ogm-core" % "2.0.1"

  lazy val neo4jHttpDriver = "org.neo4j" % "neo4j-ogm-http-driver" % "2.0.1"

  lazy val scaldi = "org.scaldi" %% "scaldi" % "0.5.7"

  lazy val apacheValidator = "commons-validator" % "commons-validator" % "1.5.0"

  lazy val apacheCodec = "commons-codec" % "commons-codec" % "1.10"

  lazy val logback = "ch.qos.logback" %  "logback-classic" % "1.1.7"

  lazy val scalactic = "org.scalactic" %% "scalactic" % "2.2.6"

}

