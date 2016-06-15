package wotgraph.app.permission.infrastructure.repository

import java.util.UUID

import wotgraph.app.permission.domain.entity.Permission
import wotgraph.app.permission.domain.repository.PermissionRepository
import wotgraph.app.permission.infrastructure.repository.neo4j.PermissionNeo4jRepository

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

  override def getAll: Future[List[Permission]] = permissionNeo4jRepository.getAll
}
