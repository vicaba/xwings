package wotgraph.app.authorization.application.service

import java.util.UUID

import wotgraph.app.authorization.domain.repository.AuthorizationRepository

import scala.concurrent.Future

class AuthorizationService(authorizationRepository: AuthorizationRepository) {
  def execute(nodeId: UUID, useCaseId: UUID): Future[Boolean] =
    authorizationRepository.isNodeAllowedToExecuteUseCase(nodeId, useCaseId)
}
