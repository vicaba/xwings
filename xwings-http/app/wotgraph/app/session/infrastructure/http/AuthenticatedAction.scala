package wotgraph.app.session.infrastructure.http

import play.api.mvc._

import scala.concurrent.Future

case class AuthenticatedRequest[A](request: Request[A], name: String) extends WrappedRequest[A](request)

object AuthenticatedAction extends ActionBuilder[AuthenticatedRequest] with Controller {

  val tokenKey = "tkn"

  override def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[Result]): Future[Result] =
    authenticate(request) match {
      case Left(ar) => block(ar)
      case Right(ur) => Future.successful(Unauthorized(""))
    }

  private def authenticate[A](request: Request[A]): AuthenticatedRequest[A] Either Request[A] =
    request.session.get(tokenKey)
      .fold[AuthenticatedRequest[A] Either Request[A]] {
      Right(request)
    } {
      name => Left(AuthenticatedRequest(request, name))
    }

}
