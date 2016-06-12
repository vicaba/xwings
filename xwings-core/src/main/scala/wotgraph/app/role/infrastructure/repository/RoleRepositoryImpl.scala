package wotgraph.app.role.infrastructure.repository

import java.util.UUID

import wotgraph.app.role.domain.entity.Role
import wotgraph.app.role.domain.repository.RoleRepository
import wotgraph.app.role.infrastructure.repository.neo4j.RoleNeo4jRepository

import scala.concurrent.{ExecutionContext, Future}

case class RoleRepositoryImpl(
                                roleNeo4jRepository: RoleNeo4jRepository
                             )(implicit ec: ExecutionContext)
  extends RoleRepository {

  override def create(role: Role): Future[Role] = roleNeo4jRepository.create(role)

  override def update(role: Role): Future[Option[Role]] = roleNeo4jRepository.update(role).map(Some(_))

  override def findById(id: UUID): Future[Option[Role]] = roleNeo4jRepository.findById(id)

  override def getAll: Future[List[Role]] = roleNeo4jRepository.getAll

  override def delete(id: UUID): Future[UUID] = roleNeo4jRepository.delete(id)
}
