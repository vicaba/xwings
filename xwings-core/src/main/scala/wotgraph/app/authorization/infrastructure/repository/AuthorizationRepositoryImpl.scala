package wotgraph.app.authorization.infrastructure.repository

import wotgraph.app.authorization.domain.repository.AuthorizationRepository
import wotgraph.app.authorization.infrastructure.repository.neo4j.AuthorizationNeo4jRepository

import scala.concurrent.{ExecutionContext, Future}

case class AuthorizationRepositoryImpl(
                                        authorizationNeo4jRepository: AuthorizationNeo4jRepository,
                                      )
                                      (implicit ec: ExecutionContext)
  extends AuthorizationRepository {


  override def hasUseCaseAllConnectionsToUser: Future[Boolean] =
    authorizationNeo4jRepository.hasUseCaseAllConnectionsToUser


}
