package edu.url.lasalle.wotgraph.infrastructure

import java.net.URI

import edu.url.lasalle.wotgraph.application.usecase.ThingUseCase
import edu.url.lasalle.wotgraph.domain.repository.thing.ThingRepository
import edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.{MongoDbConfig, ThingMongoEnvironment}
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.Neo4jConf
import edu.url.lasalle.wotgraph.infrastructure.repository.thing.ThingRepositoryImpl
import edu.url.lasalle.wotgraph.infrastructure.repository.thing.mongodb.ThingMongoDbRepository
import edu.url.lasalle.wotgraph.infrastructure.repository.thing.neo4j.ThingNeo4jRepository
import scaldi.Module

object DependencyInjector {

  val conf = AppConfig.defaultConf

  implicit val injector = new Module {

    implicit val ec = scala.concurrent.ExecutionContext.global

    val thingNeo4jConfig = Neo4jConf.Config(
      Neo4jConf.Credentials(conf.getString("neo4j.user"), conf.getString("neo4j.password")),
      URI.create(s"http://${conf.getString("neo4j.server")}"),
      List("edu.url.lasalle.wotgraph.infrastructure.repository.thing.neo4j")
    )
    val thingNeo4jRepository = ThingNeo4jRepository(thingNeo4jConfig)

    val thingMongoEnvironment = ThingMongoEnvironment(conf)
    val thingMongoDbRepository = ThingMongoDbRepository(thingMongoEnvironment.db)

    bind [ThingRepository] identifiedBy 'ThingRepository to ThingRepositoryImpl(thingNeo4jRepository, thingMongoDbRepository)
    bind [ThingUseCase] identifiedBy 'ThingUseCase to ThingUseCase(inject[ThingRepository](identified by 'ThingRepository))
  }

}
