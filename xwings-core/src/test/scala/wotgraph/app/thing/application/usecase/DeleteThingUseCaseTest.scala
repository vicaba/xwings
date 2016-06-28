package wotgraph.app.thing.application.usecase

import java.util.UUID

import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FunSuite, ParallelTestExecution}
import wotgraph.app.authorization.application.service.AuthorizationService
import wotgraph.app.thing.domain.repository.ThingRepository

import scala.concurrent.Future

class DeleteThingUseCaseTest
  extends FunSuite
    with BeforeAndAfter
    with MockFactory
    with ParallelTestExecution
    with AuthorizationServiceMock {

  var deleteThingUseCase: DeleteThingUseCase = _

  var thingRepository: ThingRepository = _

  var randomUuid = UUID.randomUUID()


  before {

    authorizationService = mock[AuthorizationServiceNoArgs]
    thingRepository = mock[ThingRepository]

  }

  after {

    authorizationService = null
    thingRepository = null
  }

  test("authorization service is called once when a delete thing use case is given the correct arguments") {
    givenAnAuthorizationServiceExecuteIsCalledOnce()
    whenExecutingADeleteThingUseCase()
  }

  test("thing repository delete is called once when the authorization service returns true") {
    givenAnAuthorizationServiceReturningTrue()
    givenAThingRepositoryDeleteIsCalledOnce()
    whenExecutingADeleteThingUseCase()
  }

  test("thing repository delete and authorization repository is not called when a wrong uuid is provided") {
    givenAnAuthorizationServiceExecuteIsNeverCalled()
    givenAThingRepositoryDeleteNeverCalled()
    whenExecutingADeleteThingUseCaseWithAnInvalidUuid()
  }

  test("thing repository delete is not called when the authorization service returns false") {
    givenAnAuthorizationServiceReturningFalse()
    givenAThingRepositoryDeleteNeverCalled()
    whenExecutingADeleteThingUseCase()
  }

  def whenExecutingADeleteThingUseCase() = {
    createDeleteThingUseCase()
    deleteThingUseCase.execute(randomUuid.toString)(randomUuid)
  }

  def whenExecutingADeleteThingUseCaseWithAnInvalidUuid() = {
    createDeleteThingUseCase()
    deleteThingUseCase.execute("invalid uuid")(randomUuid)
  }

  def createDeleteThingUseCase() = {
    deleteThingUseCase = new DeleteThingUseCase(thingRepository, authorizationService)
  }

  def givenAThingRepositoryDeleteIsCalledOnce() = {
    thingRepository.delete _ expects (*) once()
  }

  def givenAThingRepositoryDeleteNeverCalled() = {
    thingRepository.delete _ expects (*) never()
  }

}
