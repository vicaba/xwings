package edu.url.lasalle.wotgraph.infrastructure

import java.net.URI

import domain.thing.repository.ThingRepository
import edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.{MongoDbConfig, ThingsMongoEnvironment}
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.Neo4jConf
import edu.url.lasalle.wotgraph.infrastructure.thing.repository.ThingRepositoryImpl
import scaldi.Module

object DependencyInjector {

  val thingRepository: ThingRepository = {
    val conf = AppConfig.defaultConf
    val mongoDbConfig = MongoDbConfig(ThingsMongoEnvironment(AppConfig.defaultConf).db.collection("metadata"))
    val neo4jConfig = Neo4jConf.Config(
      Neo4jConf.Credentials(conf.getString("neo4j.user"), conf.getString("neo4j.password")),
      URI.create(s"http://${conf.getString("neo4j.server")}"),
      List("edu.url.lasalle.wotgraph.domain.thing")
    )
    implicit val ec = scala.concurrent.ExecutionContext.global

    val repo = ThingRepositoryImpl(neo4jConfig, mongoDbConfig)
    repo
  }

  implicit val injector = new Module {
    bind [ThingRepository] identifiedBy 'ThingRepository to thingRepository
  }

}
