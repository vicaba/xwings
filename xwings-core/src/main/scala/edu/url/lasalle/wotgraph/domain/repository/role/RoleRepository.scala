package edu.url.lasalle.wotgraph.domain.repository.role

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.entity.user.authorization.Role

import scala.concurrent.Future

trait RoleRepository {

    def createRole(role: Role): Future[Role]

    def updateRole(role: Role): Future[Option[Role]]

    def deleteRole(id: UUID): Future[UUID]

    def findRoleById(id: UUID): Future[Option[Role]]

}
