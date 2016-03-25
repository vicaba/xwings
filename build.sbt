name := "XBackend"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % "2.11.7",
  "com.typesafe.play" %% "play-ws" % "2.4.3",
  "com.typesafe.play" %% "play-json" % "2.4.3",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.2.play24",
  "com.typesafe" % "config" % "1.3.0",
  "org.neo4j" % "neo4j-ogm" % "1.1.6"
)