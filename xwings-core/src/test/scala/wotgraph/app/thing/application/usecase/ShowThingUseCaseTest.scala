package wotgraph.app.thing.application.usecase

import java.util.UUID

import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FunSuite, ParallelTestExecution}
import wotgraph.app.thing.domain.repository.ThingRepository

class ShowThingUseCaseTest
  extends FunSuite
    with BeforeAndAfter
    with MockFactory
    with ParallelTestExecution
    with AuthorizationServiceMock {

  var showThingUseCase: ShowThingUseCase = _

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

  test("authorization service is called once when a show thing use case is given the correct arguments") {
    givenAnAuthorizationServiceExecuteIsCalledOnce()
    whenExecutingAShowThingUseCase()
  }

  test("thing repository find is called once when the authorization service returns true") {
    givenAnAuthorizationServiceReturningTrue()
    givenAThingRepositoryFindIsCalledOnce()
    whenExecutingAShowThingUseCase()
  }

  test("thing repository find and authorization repository is not called when a wrong uuid is provided") {
    givenAnAuthorizationServiceExecuteIsNeverCalled()
    givenAThingRepositoryFindNeverCalled()
    whenExecutingAShowThingUseCaseWithAnInvalidUuid()
  }

  test("thing repository find is not called when the authorization service returns false") {
    givenAnAuthorizationServiceReturningFalse()
    givenAThingRepositoryFindIsCalledOnce()
    whenExecutingAShowThingUseCase()
  }

  def whenExecutingAShowThingUseCase() = {
    createDeleteThingUseCase()
    showThingUseCase.execute(randomUuid.toString)(randomUuid)
  }

  def whenExecutingAShowThingUseCaseWithAnInvalidUuid() = {
    createDeleteThingUseCase()
    showThingUseCase.execute("invalid uuid")(randomUuid)
  }

  def createDeleteThingUseCase() = {
    showThingUseCase = new ShowThingUseCase(thingRepository, authorizationService)
  }

  def givenAThingRepositoryFindIsCalledOnce() = {
    thingRepository.findById _ expects (*) once()
  }

  def givenAThingRepositoryFindNeverCalled() = {
    thingRepository.findById _ expects (*) never()
  }

}
