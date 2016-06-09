package edu.url.lasalle.wotgraph.infrastructure.repository.permission

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.entity.user.authorization.{Permission, Role}
import edu.url.lasalle.wotgraph.domain.repository.permission.PermissionRepository

import scala.concurrent.{ExecutionContext, Future}

case class PermissionRepositoryImpl(
  permissionNeo4jRepository: PermissionNeo4jRepository
)
                                   (implicit ec: ExecutionContext)
  extends PermissionRepository {

  override def findById(id: UUID): Future[Option[Permission]] = permissionNeo4jRepository.findById(id)

  override def create(perm: Permission): Future[Permission] = permissionNeo4jRepository.create(perm)


  // TODO: Check existence when updating
  override def update(perm: Permission): Future[Option[Permission]] = permissionNeo4jRepository.update(perm).map(Some(_))

  override def delete(id: UUID): Future[UUID] = permissionNeo4jRepository.delete(id)

}