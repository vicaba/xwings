package wotgraph.app.thing.application.usecase

import org.scalamock.scalatest.MockFactory
import org.scalatest.Suite
import wotgraph.app.authorization.application.service.AuthorizationService

import scala.concurrent.Future

trait AuthorizationServiceMock extends Suite with MockFactory {

  class AuthorizationServiceNoArgs extends AuthorizationService(null)

  var authorizationService: AuthorizationServiceNoArgs = _

  def givenAnAuthorizationServiceReturningTrue() = {
    authorizationService.execute _ expects(*, *) returning Future.successful(true) anyNumberOfTimes()
  }

  def givenAnAuthorizationServiceReturningFalse() = {
    authorizationService.execute _ expects(*, *) returning Future.successful(false) anyNumberOfTimes()
  }

  def givenAnAuthorizationServiceExecuteIsCalledOnce() = {
    authorizationService.execute _ expects(*, *) returning Future.successful(true) once()
  }

  def givenAnAuthorizationServiceExecuteIsNeverCalled() = {
    authorizationService.execute _ expects(*, *) never()
  }

}
