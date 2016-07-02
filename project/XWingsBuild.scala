import play.sbt._
import play.sbt.PlayImport._
import play.sbt.routes.RoutesKeys._

import sbt.Keys._
import sbt._

import Dependencies._

object XWingsBuild extends Build {

  lazy val commonSettings = Seq(
    organization := "LaSalle",
    scalaVersion := "2.11.8",
    version := "2.0-RC4"
  )

  lazy val aggregatedProjects: Seq[ProjectReference] = Seq(core)

  lazy val root = Project(
    id = "xwings",
    base = file("."),
    aggregate = aggregatedProjects,
    settings = commonSettings
  )

  lazy val core = Project(
    id = "xwings-core",
    base = file("xwings-core"),
    settings = commonSettings
  ).settings(coreDependencies: _*)

  lazy val coreDependencies = Seq(libraryDependencies ++= Seq(
    akkaStreams,
    scalaReflect,
    scaldi,
    playWS,
    playReactiveMongo,
    typesafeConfig,
    neo4jCore,
    neo4jHttpDriver,
    apacheValidator,
    logback,
    scalactic,
    scalamock % Test,
    scalatest % Test
  ))

  lazy val http = Project(
    id = "xwings-http",
    base = file("xwings-http"),
    settings = commonSettings,
    dependencies = Seq(core)
  )
    .enablePlugins(PlayScala)
    .settings(httpDependencies: _*)
    .settings(routesGenerator := InjectedRoutesGenerator, fork in sbt.Keys.run := true)
    .settings(httpSettings)

  lazy val httpDependencies = Seq(libraryDependencies ++= Seq(
    jdbc,
    cache,
    ws,
    filters,
    specs2 % Test,
    akkaRemote,
    scalactic
  ))

  lazy val httpSettings = Seq(PlayKeys.devSettings := Seq("play.server.https.port" -> "9443"))

  lazy val boostrap = Project(
    id = "xwings-bootstrap",
    base = file("xwings-bootstrap"),
    settings = commonSettings,
    dependencies = Seq(core)
  )
    .settings(bootstrapDependencies)

  lazy val bootstrapDependencies = Seq(libraryDependencies ++= Seq(
    scalamock
  ))

}