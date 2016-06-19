package wotgraph.app.user.application.usecase

import java.util.UUID

import wotgraph.toolkit.application.usecase.PermissionProvider

class ShowUserUseCase {

  def execute(id: String) = ???

}

object ShowUserUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("f46c5ab4-b5f0-44a5-8842-6ea2a050dcc0")
}
