package edu.url.lasalle.wotgraph.infrastructure

import edu.url.lasalle.wotgraph.application.usecase.ThingUseCase
import edu.url.lasalle.wotgraph.domain.repository.thing.ThingRepository
import edu.url.lasalle.wotgraph.domain.repository.user.UserRepository
import edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.ThingMongoEnvironment
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.Neo4jConf
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.helpers.Neo4jOGMHelper
import edu.url.lasalle.wotgraph.infrastructure.repository.permission.PermissionNeo4jRepository
import edu.url.lasalle.wotgraph.infrastructure.repository.thing.{ThingMongoDbRepository, ThingNeo4jRepository, ThingRepositoryImpl}
import edu.url.lasalle.wotgraph.infrastructure.repository.user.{UserNeo4jRepository, UserRepositoryImpl}
import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.session.{Session => Neo4jSession}
import scaldi.Module

object DependencyInjector {

  val conf = AppConfig.defaultConf

  implicit val injector = new Module {

    implicit val ec = scala.concurrent.ExecutionContext.global

    val thingMongoEnvironment = ThingMongoEnvironment(conf)

    bind[Neo4jConf.Config] identifiedBy 'Neo4jConfig to {
      val configuration = new Configuration()
      configuration.set("driver", conf.getString("neo4j.ogm.driver"))
      configuration.set("URI", conf.getString("neo4j.ogm.uri"))
      Neo4jConf.Config(configuration, List("edu.url.lasalle.wotgraph.infrastructure.repository.thing"))
    }
    bind[Neo4jSession] identifiedBy 'Neo4jSession to Neo4jOGMHelper.getSession(
      inject[Neo4jConf.Config](identified by 'Neo4jConfig)
    )

    bind[ThingNeo4jRepository] identifiedBy 'ThingNeo4jRepository to ThingNeo4jRepository(
      inject[Neo4jSession](identified by 'Neo4jSession)
    )
    bind[ThingMongoDbRepository] identifiedBy 'ThingMongoDbRepository to ThingMongoDbRepository(thingMongoEnvironment.db)
    bind[ThingRepository] identifiedBy 'ThingRepository to ThingRepositoryImpl(
      inject[ThingNeo4jRepository](identified by 'ThingNeo4jRepository),
      inject[ThingMongoDbRepository](identified by 'ThingMongoDbRepository)
    )

    bind[ThingUseCase] identifiedBy 'ThingUseCase to ThingUseCase(inject[ThingRepository](identified by 'ThingRepository))

    bind[UserNeo4jRepository] identifiedBy 'UserNeo4jRepository to UserNeo4jRepository(
      inject[Neo4jSession](identified by 'Neo4jSession)
    )
    bind[UserRepository] identifiedBy 'UserRepository to UserRepositoryImpl(
      inject[UserNeo4jRepository](identified by 'UserNeo4jRepository)
    )

    bind[PermissionNeo4jRepository] identifiedBy 'PermissionNeo4jRepository to PermissionNeo4jRepository(
      inject[Neo4jSession](identified by 'Neo4jSession)
    )


  }

}
