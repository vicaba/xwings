package wotgraph.app.role.domain.repository

import java.util.UUID

import wotgraph.app.role.domain.entity.Role

import scala.concurrent.Future

trait RoleRepository {

    def create(role: Role): Future[Role]

    def update(role: Role): Future[Option[Role]]

    def delete(id: UUID): Future[UUID]

    def findById(id: UUID): Future[Option[Role]]

}
