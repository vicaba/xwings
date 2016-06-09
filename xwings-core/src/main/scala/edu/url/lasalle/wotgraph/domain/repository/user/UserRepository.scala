package edu.url.lasalle.wotgraph.domain.repository.user

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.entity.user.User
import org.scalactic.{Every, Or}

import scala.concurrent.Future


trait UserRepository {

  def create(user: User):  Future[User Or Every[String]]

  def update(user: User): Future[Option[User]]

  def delete(id: UUID): Future[UUID]

  def findById(id: UUID): Future[Option[User]]
}
