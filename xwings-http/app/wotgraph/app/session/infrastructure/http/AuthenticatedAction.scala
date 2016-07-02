package wotgraph.app.session.infrastructure.http

import java.util.UUID

import org.scalactic.{Bad, Good, One, Or}
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

  def sessionAuthenticate(request: RequestHeader): Future[UUID Either String] = {
    request.session.get(tokenKey).fold[Future[UUID Either String]] {
      Future.successful(Right("Can't authenticate"))
    } {
      token: String => getUserId(token).map(Left(_))
    }
  }

  def authenticate[A](request: Request[A]): Future[AuthenticatedRequest[A] Either Request[A]] =
    sessionAuthenticate(request).map {
      case Left(id) => Left(AuthenticatedRequest(request, id))
      case Right(m) => Right(request)
    }

  private def getUserId(hash: String): Future[UUID] = Future.successful {
    UUID.fromString(decrypt(hash))
  }

}

