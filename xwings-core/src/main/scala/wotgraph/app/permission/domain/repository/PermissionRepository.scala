package wotgraph.app.permission.domain.repository

import java.util.UUID

import wotgraph.app.permission.domain.entity.Permission
import scala.concurrent.Future


trait PermissionRepository {

  def findById(id: UUID): Future[Option[Permission]]

  def create(perm: Permission): Future[Permission]

  def update(perm: Permission): Future[Option[Permission]]

  def delete(id: UUID): Future[UUID]

  def getAll: Future[List[Permission]]

}
