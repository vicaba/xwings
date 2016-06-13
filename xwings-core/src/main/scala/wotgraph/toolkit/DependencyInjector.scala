package wotgraph.toolkit

import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.session.{Session => Neo4jSession}
import scaldi.Module
import wotgraph.app.permission.domain.repository.PermissionRepository
import wotgraph.app.permission.infrastructure.repository.PermissionRepositoryImpl
import wotgraph.app.permission.infrastructure.repository.neo4j.PermissionNeo4jRepository
import wotgraph.app.thing.application.usecase._
import wotgraph.app.thing.domain.repository.ThingRepository
import wotgraph.app.thing.infrastructure.repository.ThingRepositoryImpl
import wotgraph.app.thing.infrastructure.repository.mongodb.ThingMongoDbRepository
import wotgraph.app.thing.infrastructure.repository.neo4j.ThingNeo4jRepository
import wotgraph.app.user.domain.repository.UserRepository
import wotgraph.app.user.infrastructure.repository.UserRepositoryImpl
import wotgraph.app.user.infrastructure.repository.neo4j.UserNeo4jRepository
import wotgraph.toolkit.repository.mongodb.ThingMongoEnvironment
import wotgraph.toolkit.repository.neo4j.Neo4jConf
import wotgraph.toolkit.repository.neo4j.helpers.Neo4jOGMHelper

object DependencyInjector {

  val conf = AppConfig.defaultConf

  implicit val injector = new Module {

    implicit val ec = scala.concurrent.ExecutionContext.global

    val thingMongoEnvironment = ThingMongoEnvironment(conf)

    bind[Neo4jConf.Config] identifiedBy 'Neo4jConfig to {
      val configuration = new Configuration()
      configuration.set("driver", conf.getString("neo4j.ogm.driver"))
      configuration.set("URI", conf.getString("neo4j.ogm.uri"))
      Neo4jConf.Config(configuration, List("wotgraph.toolkit.repository.neo4j.entity"))
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

    bind[CreateThingUseCase] identifiedBy 'CreateThingUseCase to new CreateThingUseCase(inject[ThingRepository](identified by 'ThingRepository))
    bind[ShowThingUseCase] identifiedBy 'ShowThingUseCase to new ShowThingUseCase(inject[ThingRepository](identified by 'ThingRepository))
    bind[UpdateThingUseCase] identifiedBy 'UpdateThingUseCase to new UpdateThingUseCase(inject[ThingRepository](identified by 'ThingRepository))
    bind[ListThingsUseCase] identifiedBy 'ListThingsUseCase to new ListThingsUseCase(inject[ThingRepository](identified by 'ThingRepository))
    bind[DeleteThingUseCase] identifiedBy 'DeleteThingUseCase to new DeleteThingUseCase(inject[ThingRepository](identified by 'ThingRepository))
    bind[ExecuteThingActionUseCase] identifiedBy 'ExecuteThingActionUseCase to new ExecuteThingActionUseCase(inject[ThingRepository](identified by 'ThingRepository))

    bind[UserNeo4jRepository] identifiedBy 'UserNeo4jRepository to UserNeo4jRepository(
      inject[Neo4jSession](identified by 'Neo4jSession)
    )
    bind[UserRepository] identifiedBy 'UserRepository to UserRepositoryImpl(
      inject[UserNeo4jRepository](identified by 'UserNeo4jRepository)
    )

    bind[PermissionNeo4jRepository] identifiedBy 'PermissionNeo4jRepository to PermissionNeo4jRepository(
      inject[Neo4jSession](identified by 'Neo4jSession)
    )

    bind[PermissionRepository] identifiedBy 'PermissionRepository to PermissionRepositoryImpl(
      inject[PermissionNeo4jRepository](identified by 'PermissionNeo4jRepository)
    )

  }

}
