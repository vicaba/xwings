package wotgraph.app.authorization.domain.repository

import java.util.UUID

import scala.concurrent.Future


trait AuthorizationRepository {

  def isNodeAllowedToExecuteUseCase(nodeId: UUID, useCaseId: UUID): Future[Boolean]

}
