package wotgraph.app.thing.application.usecase

import java.util.UUID

import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FunSuite, ParallelTestExecution}
import wotgraph.app.thing.domain.repository.ThingRepository


class ListThingsUseCaseTest
  extends FunSuite
    with BeforeAndAfter
    with MockFactory
    with ParallelTestExecution
    with AuthorizationServiceMock {

  var listThingsUseCase: ListThingsUseCase = _


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

  test("authorization service is called once") {
    givenAnAuthorizationServiceExecuteIsCalledOnce()
    whenExecutingADeleteThingUseCase()
  }

  test("thing repository list is called once when the authorization service returns true") {
    givenAnAuthorizationServiceReturningTrue()
    givenAThingRepositoryGetAllIsCalledOnce()
    whenExecutingADeleteThingUseCase()
  }

  test("thing repository list is not called when the authorization service returns false") {
    givenAnAuthorizationServiceReturningFalse()
    givenAThingRepositoryGetAllIsNeverCalled()
    whenExecutingADeleteThingUseCase()
  }

  def whenExecutingADeleteThingUseCase() = {
    createDeleteThingUSeCase()
    listThingsUseCase.execute()(randomUuid)
  }

  def createDeleteThingUSeCase() = {
    listThingsUseCase = new ListThingsUseCase(thingRepository, authorizationService)
  }

  def givenAThingRepositoryGetAllIsCalledOnce() = {
    thingRepository.getAll _ expects(*, *) once()
  }

  def givenAThingRepositoryGetAllIsNeverCalled() = {
    thingRepository.getAll _ expects(*, *) never()
  }

}
