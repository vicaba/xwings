package edu.url.lasalle.wotgraph.infrastructure

import domain.thing.repository.ThingRepository
import edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.{MongoDbConfig, ThingsMongoEnvironment}
import edu.url.lasalle.wotgraph.infrastructure.thing.repository.ThingRepositoryImpl
import scaldi.Module

object DependencyInjector {

  val thingRepository: ThingRepository = {
    val conf = AppConfig.defaultConf
    val mongoDbConfig = MongoDbConfig(ThingsMongoEnvironment(AppConfig.defaultConf).db.collection("metadata"))
    implicit val ec = scala.concurrent.ExecutionContext.global
    val repo = ThingRepositoryImpl(mongoDbConfig)
    repo
  }

  implicit val injector = new Module {
    bind [ThingRepository] identifiedBy 'ThingRepository to thingRepository
  }

}
