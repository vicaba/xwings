name := "XBackend"

version := "1.0"

scalaVersion := "2.11.7"

val xbackend = Project(id = "xbackend", base = file("."))

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % "2.11.7",
  "com.typesafe.play" %% "play-ws" % "2.4.3",
  "com.typesafe.play" %% "play-json" % "2.4.3",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.2.play24",
  "com.typesafe" % "config" % "1.3.0",
  "org.neo4j" % "neo4j-ogm-core" % "2.0.1",
  "org.neo4j" % "neo4j-ogm-http-driver" % "2.0.1",
  "org.scaldi" %% "scaldi" % "0.5.7"
)