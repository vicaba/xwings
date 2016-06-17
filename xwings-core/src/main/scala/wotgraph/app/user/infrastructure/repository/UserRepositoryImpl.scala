package wotgraph.app.user.infrastructure.repository

import java.util.UUID

import wotgraph.app.user.domain.entity.User
import wotgraph.app.user.domain.repository.UserRepository
import wotgraph.toolkit.DependencyInjector._
import org.scalactic.{Every, Or}
import scaldi.Injectable._
import wotgraph.app.error.{AppError, StorageError}
import wotgraph.app.role.domain.entity.Role
import wotgraph.app.user.infrastructure.repository.neo4j.UserNeo4jRepository

import scala.concurrent.{Await, ExecutionContext, Future}


case class UserRepositoryImpl(
                               userNeo4jRepository: UserNeo4jRepository
                             )
                             (implicit ec: ExecutionContext)
  extends UserRepository {

  override def findById(id: UUID): Future[Option[User]] = userNeo4jRepository.findById(id)

  override def create(user: User): Future[User Or Every[StorageError]] = userNeo4jRepository.create(user)

  override def update(user: User): Future[User Or Every[StorageError]] = userNeo4jRepository.update(user)

  override def delete(id: UUID): Future[UUID Or Every[StorageError]] = userNeo4jRepository.delete(id)

  override def findByCredentials(name: String, password: String): Future[Option[User]] =
    userNeo4jRepository.findByCredentials(name, password)

  override def getAll: Future[List[User]] = userNeo4jRepository.getAll
}