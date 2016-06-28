
package wotgraph.app.thing.application.usecase

import java.util.UUID

import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FunSuite, ParallelTestExecution}
import play.api.libs.json.Json
import wotgraph.app.authorization.application.service.AuthorizationService
import wotgraph.app.thing.application.usecase.dto.CreateThing
import wotgraph.app.thing.domain.entity.{Metadata, Thing}
import wotgraph.app.thing.domain.repository.ThingRepository
import wotgraph.app.thing.infrastructure.service.thing.ThingTransformer

import scala.concurrent.Future

class CreateThingUseCaseTest extends FunSuite with BeforeAndAfter with MockFactory with ParallelTestExecution {

  var createThingUseCase: CreateThingUseCase = _


  object Mock {

    class AuthorizationServiceNoArgs extends AuthorizationService(null)

    var authorizationService: AuthorizationServiceNoArgs = _

    var thingRepository: ThingRepository = _

    var thingTransformer: ThingTransformer = _

    var createThing: CreateThing = _

    var randomUuid = UUID.randomUUID()

  }

  before {
    import Mock._

    authorizationService = mock[AuthorizationServiceNoArgs]
    thingRepository = mock[ThingRepository]
    thingTransformer = mock[ThingTransformer]
    createThing = CreateThing(Metadata(Json.obj()))

  }

  after {
    import Mock._

    authorizationService = null
    thingRepository = null
    thingTransformer = null
    createThing = null
  }

/*  test("authorization service is called once when a CreateThing command is received") {
    givenAnAuthorizationServiceExecuteIsCalledOnce()
    whenExecutingACreateThingUseCase()
  }

  test("thing transformer apply is called once when a CreateThing command is received and the authorization service returns true") {
    givenAnAuthorizationServiceReturningTrue()
    givenAThingTransformerApplyIsCalledOnce()
    whenExecutingACreateThingUseCase()
  }

  test("thing repository create is called once when a CreateThing command is received and the authorization service returns true") {
    givenAnAuthorizationServiceReturningTrue()
    givenAThingRepositoryCreateIsCalledOnce()
    whenExecutingACreateThingUseCase()
  }*/

  def givenAnAuthorizationServiceExecuteIsCalledOnce() = {
    Mock.authorizationService.execute _ expects(*, *) returning Future.successful(true) once()
  }

  def whenExecutingACreateThingUseCase() = {
    createCreateThingUseCase()
    createThingUseCase.execute(Mock.createThing)(Mock.randomUuid)
  }

  def createCreateThingUseCase() = {
    createThingUseCase = new CreateThingUseCase(Mock.thingRepository, Mock.authorizationService, Mock.thingTransformer)
  }

  def givenAnAuthorizationServiceReturningTrue() = {
    Mock.authorizationService.execute _ expects(*, *) returning Future.successful(true) anyNumberOfTimes()
  }

  def givenAThingRepositoryCreateIsCalledOnce() = {
    Mock.thingRepository.create _ expects (*) once()
  }

  def givenAThingTransformerApplyIsCalledOnce() = {
    Mock.thingTransformer.apply _ expects (*) once()
  }

}

