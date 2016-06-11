package wotgraph.app.user.infrastructure.repository

import java.util.UUID

import wotgraph.app.user.domain.entity.User
import wotgraph.app.user.domain.repository.UserRepository
import wotgraph.app.user.infrastructure.repository.user.neo4j.UserNeo4jRepository
import wotgraph.toolkit.DependencyInjector._
import org.scalactic.{Every, Or}
import scaldi.Injectable._
import wotgraph.app.role.domain.entity.Role

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

object Main {

  def main(args: Array[String]) {
    implicit val ec = scala.concurrent.ExecutionContext.global
    import scala.concurrent.duration._

    val repo: UserRepository = inject[UserRepository](identified by 'UserRepository)

    val f = repo.create(User(role = Role(name = "SomeRole")))

    Await.result(f, 5 seconds)

  }

}
