package wotgraph.app.user.domain.repository

import java.util.UUID

import org.scalactic.{Every, Or}
import wotgraph.app.error.{AppError, StorageError}
import wotgraph.app.user.domain.entity.User

import scala.concurrent.Future

trait UserRepository {

  def create(user: User):  Future[User Or Every[StorageError]]

  def update(user: User): Future[User Or Every[AppError]]

  def delete(id: UUID): Future[UUID Or Every[AppError]]

  def findById(id: UUID): Future[Option[User]]

  def findByCredentials(name: String, password: String): Future[Option[User]]

  def getAll: Future[List[User]]

}
