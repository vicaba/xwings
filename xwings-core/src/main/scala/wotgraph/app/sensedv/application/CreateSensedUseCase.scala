package wotgraph.app.sensedv.application

import java.util.UUID

import org.scalactic.{Every, Good, Or}
import wotgraph.app.authorization.application.service.AuthorizationService
import wotgraph.app.error.AppError
import wotgraph.app.role.application.dto.CreateRole
import wotgraph.app.role.domain.entity.Role
import wotgraph.app.sensedv.application.usecase.dto.CreateSensed
import wotgraph.app.sensedv.domain.Sensed
import wotgraph.app.sensedv.domain.repository.SensedRepository
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.Future

/**
  * Created by vicaba on 20/06/16.
  */
class CreateSensedUseCase(sensedRepository: SensedRepository, authorizationService: AuthorizationService) {
  def execute(c: CreateSensed)(executorAgentId: UUID): Future[Sensed Or Every[AppError]] =
    AuthorizationService.execute(authorizationService, executorAgentId, CreateSensedUseCase.permission.id) {
      sensedRepository.create()
    }
}

object CreateSensedUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("bdb1d552-80af-4a9f-958b-f779e492a4b2")
}
