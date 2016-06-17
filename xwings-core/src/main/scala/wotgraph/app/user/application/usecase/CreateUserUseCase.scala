package wotgraph.app.user.application.usecase


import org.scalactic.{Every, Or}
import wotgraph.app.error.AppError
import wotgraph.app.user.application.usecase.dto.CreateUser
import wotgraph.app.user.domain.entity.User
import wotgraph.app.user.domain.repository.UserRepository
import wotgraph.toolkit.crypt.Hasher

import scala.concurrent.Future


class CreateUserUseCase(userRepository: UserRepository, hash: Hasher.PrebuiltHash) {

  def execute(c: CreateUser): Future[User Or Every[AppError]] = {
    val user = CreateUser.toUser(c)
    val password = hash(user.password.toCharArray)
    userRepository.create(user.copy(password = password))
  }
}
