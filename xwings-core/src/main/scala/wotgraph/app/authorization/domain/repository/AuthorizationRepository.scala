package wotgraph.app.authorization.domain.repository

import scala.concurrent.Future


trait AuthorizationRepository {

  def hasUseCaseAllConnectionsToUser: Future[Boolean]

}
