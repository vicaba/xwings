package wotgraph.app.user.application.usecase

import wotgraph.app.user.domain.repository.UserRepository

class ListUsersUseCase(userRepository: UserRepository) {

  def execute() = userRepository.getAll

}
