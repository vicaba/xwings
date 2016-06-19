package wotgraph.app.thing.application.usecase

import java.util.UUID

import org.scalactic.{Every, Good, Or}
import play.api.libs.iteratee.Enumerator
import wotgraph.app.authorization.application.service.AuthorizationService
import wotgraph.app.error.AppError
import wotgraph.app.thing.application.usecase.dto.GetThings
import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.thing.domain.repository.ThingRepository
import wotgraph.app.user.domain.entity.User
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class ListThingsUseCase(thingRepository: ThingRepository, authorizationService: AuthorizationService) {

  def execute(g: GetThings = GetThings(0, 100))(userId: User.Id): Future[List[Thing] Or Every[AppError]] =
    AuthorizationService.executeAsync(authorizationService, userId, ListThingsUseCase.permission.id) {
      thingRepository.getAll(g.itemPerPage * g.pageNumber, g.itemPerPage).map(Good(_))
    }

  def executeAsStream(userId: User.Id): Future[Enumerator[Thing] Or Every[AppError]] =
    AuthorizationService.execute(authorizationService, userId, ListThingsUseCase.permission.id) {
      Good(thingRepository.getAllAsStream)
    }

}

object ListThingsUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("6006f27b-edad-4b91-aee4-6f4c477cad9a")
}
