package edu.url.lasalle.wotgraph.infrastructure.repository.user

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.entity.user.authorization.Role
import edu.url.lasalle.wotgraph.domain.repository.user.UserRepository
import edu.url.lasalle.wotgraph.domain.entity.user.User
import scaldi.Injectable._
import edu.url.lasalle.wotgraph.infrastructure.DependencyInjector._

import scala.concurrent.{ExecutionContext, Future}


case class UserRepositoryImpl(
                               userNeo4jRepository: UserNeo4jRepository
                             )
                             (implicit ec: ExecutionContext)
  extends UserRepository {

  override def findById(id: UUID): Future[Option[User]] = {
    userNeo4jRepository.findById(id)
  }


  override def create(user: User): Future[User] = {
    userNeo4jRepository.create(user)
  }

  override def update(user: User): Future[Option[User]] = ???

  override def delete(id: UUID): Future[UUID] = ???

}

object Main {

  def main(args: Array[String]) {
    implicit val ec = scala.concurrent.ExecutionContext.global
    import scala.concurrent.duration._

    val repo: UserRepository = inject[UserRepository](identified by 'UserRepository)

    repo.create(User(role = Role(name = "SomeRole")))
  }

}
