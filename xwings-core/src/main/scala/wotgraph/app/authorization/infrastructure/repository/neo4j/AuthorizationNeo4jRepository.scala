package wotgraph.app.authorization.infrastructure.repository.neo4j

import org.neo4j.ogm.session.Session
import wotgraph.toolkit.repository.neo4j.helpers.Neo4jOGMHelper

import scala.concurrent.{ExecutionContext, Future}


case class AuthorizationNeo4jRepository(
                                     session: Session
                                   )
                                   (implicit ec: ExecutionContext)
  extends Neo4jOGMHelper {

  def hasUseCaseAllConnectionsToUser: Future[Boolean] = ???

}
