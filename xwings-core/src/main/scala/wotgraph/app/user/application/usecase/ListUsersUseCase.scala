package wotgraph.app.user.application.usecase

import java.util.UUID

import org.scalactic.Good
import wotgraph.app.authorization.application.service.AuthorizationService
import wotgraph.app.user.domain.repository.UserRepository
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.ExecutionContext.Implicits.global

class ListUsersUseCase(userRepository: UserRepository, authorizationService: AuthorizationService) {

  def execute()(executorAgentId: UUID) =
    AuthorizationService.asyncExecute(authorizationService, executorAgentId, ListUsersUseCase.permission.id) {
      userRepository.getAll.map(Good(_))
    }
}

object ListUsersUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("dc9d03c4-40f8-4634-9d2e-174abeaaa162")
}
