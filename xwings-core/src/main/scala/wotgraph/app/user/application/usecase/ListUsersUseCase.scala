package wotgraph.app.user.application.usecase

import java.util.UUID

import wotgraph.app.user.domain.repository.UserRepository
import wotgraph.toolkit.application.usecase.PermissionProvider

class ListUsersUseCase(userRepository: UserRepository) {

  def execute() = userRepository.getAll

}

object ListUsersUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("dc9d03c4-40f8-4634-9d2e-174abeaaa162")
}
