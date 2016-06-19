package wotgraph.app.session.infrastructure.http

import java.util.UUID

import play.api.mvc._
import scaldi.Injectable._
import wotgraph.app.user.domain.entity.User
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class AuthenticatedRequest[A](request: Request[A], userId: User.Id) extends WrappedRequest[A](request)

object AuthenticatedAction extends ActionBuilder[AuthenticatedRequest] with Controller {

  val tokenKey = "tkn"

  lazy val decrypt: String => String = inject[String => String](identified by 'SessionDecrypter)


  override def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[Result]): Future[Result] =
    authenticate(request).flatMap {
      case Left(ar) => block(ar)
      case Right(ur) => Future.successful(Unauthorized(""))
    }

  private def authenticate[A](request: Request[A]): Future[AuthenticatedRequest[A] Either Request[A]] =
    request.session.get(tokenKey)
      .fold[Future[AuthenticatedRequest[A] Either Request[A]]] {
      println(request.session)
      Future.successful(Right(request))
    } {
      tokenValue =>
        getUserId(tokenValue).map { id =>
          Left(AuthenticatedRequest(request, id))
        } recover { case _ => Right(request) }
    }

  def getUserId(hash: String): Future[User.Id] = Future {
    UUID.fromString(decrypt(hash))
  }

}

