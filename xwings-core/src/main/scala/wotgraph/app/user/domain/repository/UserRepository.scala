package wotgraph.app.user.domain.repository

import java.util.UUID

import wotgraph.app.user.domain.entity.User
import org.scalactic.{Every, Or}

import scala.concurrent.Future


trait UserRepository {

  def create(user: User):  Future[User Or Every[String]]

  def update(user: User): Future[Option[User]]

  def delete(id: UUID): Future[UUID]

  def findById(id: UUID): Future[Option[User]]

  def findByCredentials(name: String, password: String): Future[Option[User]]

}
