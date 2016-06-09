package edu.url.lasalle.wotgraph.domain.repository.permission

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.entity.user.authorization.{Permission, Role}

import scala.concurrent.Future


trait PermissionRepository {

  def findById(id: UUID): Future[Option[Permission]]

  def create(perm: Permission): Future[Permission]

  def update(perm: Permission): Future[Option[Permission]]

  def delete(id: UUID): Future[UUID]

}
