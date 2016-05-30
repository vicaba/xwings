package edu.url.lasalle.wotgraph.infrastructure

import java.net.URI

import edu.url.lasalle.wotgraph.application.usecase.ThingUseCase
import edu.url.lasalle.wotgraph.domain.repository.thing.ThingRepository
import edu.url.lasalle.wotgraph.domain.repository.user.UserRepository
import edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.{MongoDbConfig, ThingMongoEnvironment}
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.Neo4jConf
import edu.url.lasalle.wotgraph.infrastructure.repository.thing.{ThingMongoDbRepository, ThingNeo4jRepository, ThingRepositoryImpl}
import edu.url.lasalle.wotgraph.infrastructure.repository.user.{UserNeo4jRepository, UserRepositoryImpl}
import org.neo4j.ogm.config.Configuration
import scaldi.Module

object DependencyInjector {

  val conf = AppConfig.defaultConf

  implicit val injector = new Module {

    implicit val ec = scala.concurrent.ExecutionContext.global

    val thingNeo4jConfig = {
      val configuration = new Configuration()
      configuration.set("driver", conf.getString("neo4j.ogm.driver"))
      configuration.set("URI", conf.getString("neo4j.ogm.uri"))
      Neo4jConf.Config(configuration, List("edu.url.lasalle.wotgraph.infrastructure.repository.thing"))
    }
    val thingNeo4jRepository = ThingNeo4jRepository(thingNeo4jConfig)

    val thingMongoEnvironment = ThingMongoEnvironment(conf)
    val thingMongoDbRepository = ThingMongoDbRepository(thingMongoEnvironment.db)

    val userNeo4jRepository = UserNeo4jRepository(thingNeo4jConfig)

    bind[ThingRepository] identifiedBy 'ThingRepository to ThingRepositoryImpl(thingNeo4jRepository, thingMongoDbRepository)
    bind[ThingUseCase] identifiedBy 'ThingUseCase to ThingUseCase(inject[ThingRepository](identified by 'ThingRepository))

    bind[UserRepository] identifiedBy 'UserRepository to UserRepositoryImpl(userNeo4jRepository)
  }

}
