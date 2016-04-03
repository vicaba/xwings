package edu.url.lasalle.wotgraph.infrastructure

import java.net.URI

import edu.url.lasalle.wotgraph.application.usecase.ThingUseCase
import edu.url.lasalle.wotgraph.domain.repository.thing.ThingRepository
import edu.url.lasalle.wotgraph.infrastructure.repository.action.ActionMongoRepository
import edu.url.lasalle.wotgraph.infrastructure.repository.metadata.MetadataMongoRepository
import edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.{MongoDbConfig, ThingsMongoEnvironment}
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.Neo4jConf
import edu.url.lasalle.wotgraph.infrastructure.repository.thing.ThingRepositoryImpl
import scaldi.Injectable._
import scaldi.Module

object DependencyInjector {

  val thingsMongoEnvironment = ThingsMongoEnvironment(AppConfig.defaultConf)

  val metadataMongoRepository = {
    implicit val ec = scala.concurrent.ExecutionContext.global
    MetadataMongoRepository(thingsMongoEnvironment.db)
  }

  val actionMongoRepository = {
    implicit val ec = scala.concurrent.ExecutionContext.global
    ActionMongoRepository(thingsMongoEnvironment.db)
  }

  val thingRepository: ThingRepository = {
    val conf = AppConfig.defaultConf
    val neo4jConfig = Neo4jConf.Config(
      Neo4jConf.Credentials(conf.getString("neo4j.user"), conf.getString("neo4j.password")),
      URI.create(s"http://${conf.getString("neo4j.server")}"),
      List("edu.url.lasalle.wotgraph.infrastructure.repository.thing.neo4j")
    )
    implicit val ec = scala.concurrent.ExecutionContext.global

    val repo = ThingRepositoryImpl(neo4jConfig, metadataMongoRepository, actionMongoRepository)
    repo
  }

  implicit val injector = new Module {

    bind [ThingRepository] identifiedBy 'ThingRepository to thingRepository
    bind [ThingUseCase] identifiedBy 'ThingUseCase to ThingUseCase(inject[ThingRepository](identified by 'ThingRepository))
  }

}
