package wotgraph.app.authorization.infrastructure.repository

import java.util.UUID

import wotgraph.app.authorization.domain.repository.AuthorizationRepository
import wotgraph.app.authorization.infrastructure.repository.neo4j.AuthorizationNeo4jRepository
import wotgraph.app.user.domain.entity.User

import scala.concurrent.{ExecutionContext, Future}

case class AuthorizationRepositoryImpl(
                                        authorizationNeo4jRepository: AuthorizationNeo4jRepository
                                      )
                                      (implicit ec: ExecutionContext)
  extends AuthorizationRepository {


  def isNodeAllowedToExecuteUseCase(nodeId: UUID, useCaseId: UUID): Future[Boolean] =
    authorizationNeo4jRepository.isUserAllowedToExecuteUseCase(nodeId, useCaseId)


}
