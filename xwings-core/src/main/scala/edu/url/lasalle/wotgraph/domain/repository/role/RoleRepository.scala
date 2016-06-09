package edu.url.lasalle.wotgraph.domain.repository.role

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.entity.user.authorization.Role

import scala.concurrent.Future

trait RoleRepository {

    def create(role: Role): Future[Role]

    def update(role: Role): Future[Option[Role]]

    def delete(id: UUID): Future[UUID]

    def findById(id: UUID): Future[Option[Role]]

}
