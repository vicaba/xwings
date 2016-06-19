package wotgraph.app.authorization.infrastructure.repository.neo4j

import java.util.UUID

import org.neo4j.ogm.session.Session
import wotgraph.app.permission.infrastructure.repository.neo4j.PermissionNeo4jRepository
import wotgraph.app.permission.infrastructure.serialization.keys.PermissionKeys
import wotgraph.app.user.infrastructure.repository.neo4j.UserNeo4jRepository
import wotgraph.app.user.infrastructure.serialization.keys.UserKeys
import wotgraph.toolkit.repository.neo4j.helpers.Neo4jOGMHelper
import scala.collection.JavaConverters._

import scala.concurrent.{ExecutionContext, Future}


case class AuthorizationNeo4jRepository(
                                         session: Session
                                       )
                                       (implicit ec: ExecutionContext)
  extends Neo4jOGMHelper {

  def isUserAllowedToExecuteUseCase(nodeId: UUID, useCaseId: UUID): Future[Boolean] = {

    val permLabel = PermissionNeo4jRepository.Keys.PermLabel
    val permIdKey = PermissionKeys.Id
    val permIdPlaceholder = s"p$permIdKey"

    val userLabel = UserNeo4jRepository.Keys.UserLabel
    val userIdKey = UserKeys.Id

    val query =
      s"""
         |MATCH (p:$permLabel) WHERE p.$permIdKey = ${n(permIdPlaceholder)}
         |MATCH (u:$userLabel)-[*2]-(p) WHERE u.$userIdKey = ${n(userIdKey)}
         |RETURN p.$permIdKey AS $permIdKey
       """.stripMargin

    val params = Map(
      permIdPlaceholder -> useCaseId.toString,
      userIdKey -> nodeId.toString
    )

    Future {
      val queryResult = session.query(query, params.asJava)
      val result = resultCollectionAsScalaCollection(queryResult)
      result.headOption.fold(false) { map =>
        map.get(permIdKey) match {
          case Some(p) => if (p == null) false else true
          case None => false
        }
      }
    } recover { case e: Throwable => throw e }
  }

}
