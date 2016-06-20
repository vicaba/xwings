package wotgraph.app.sensedv.application

import java.util.UUID

import org.scalactic._
import wotgraph.app.authorization.application.service.AuthorizationService
import wotgraph.app.error.{AppError, ValidationError}
import wotgraph.app.sensedv.application.usecase.dto.CreateSensedValue
import wotgraph.app.sensedv.domain.SensedValue
import wotgraph.app.sensedv.domain.repository.SensedValueRepository
import wotgraph.app.thing.application.service.action.ThingAndAction
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
  * Created by vicaba on 20/06/16.
  */
class CreateSensedValueUseCase(sensedValueRepository: SensedValueRepository, authorizationService: AuthorizationService) {

  def execute(c: CreateSensedValue,
              thingId: String,
              actionName: String)
             (executorAgentId: UUID): Future[SensedValue Or Every[AppError]] =

    Try(UUID.fromString(thingId)) match {
      case Failure(_) => Future.successful(Bad(One(ValidationError.WrongUuidFormat)))
      case Success(uuid) =>
        AuthorizationService.asyncExecute(authorizationService, executorAgentId, CreateSensedValueUseCase.permission.id) {
          val sv = CreateSensedValue.toSensedValue(c, uuid, actionName)
          sensedValueRepository.create(sv)
        }
    }
}

object CreateSensedValueUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("bdb1d552-80af-4a9f-958b-f779e492a4b2")
}

