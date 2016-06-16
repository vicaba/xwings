package wotgraph.app.user.infrastructure.repository

import java.util.UUID

import wotgraph.app.user.domain.entity.User
import wotgraph.app.user.domain.repository.UserRepository
import wotgraph.toolkit.DependencyInjector._
import org.scalactic.{Every, Or}
import scaldi.Injectable._
import wotgraph.app.role.domain.entity.Role
import wotgraph.app.user.infrastructure.repository.neo4j.UserNeo4jRepository

import scala.concurrent.{Await, ExecutionContext, Future}


case class UserRepositoryImpl(
                               userNeo4jRepository: UserNeo4jRepository
                             )
                             (implicit ec: ExecutionContext)
  extends UserRepository {

  override def findById(id: UUID): Future[Option[User]] = userNeo4jRepository.findById(id)

  override def create(user: User):  Future[User Or Every[String]] = userNeo4jRepository.create(user)

  // TODO: Check if user exists
  override def update(user: User): Future[Option[User]] = userNeo4jRepository.update(user).map(Some(_))

  override def delete(id: UUID): Future[UUID] = userNeo4jRepository.delete(id)

}