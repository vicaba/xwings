package wotgraph.app.error

sealed trait AppError { val msg: String }

sealed trait StorageError extends AppError

case class Storage(override val msg: String) extends StorageError

sealed trait ValidationError extends AppError

object ValidationError {
  val WrongUuidFormat = Validation("wrong UUID format")
}

case class Validation(override val msg: String) extends ValidationError

sealed trait AuthorizationError extends AppError

object AuthorizationError {
  val NotAuthorized = AuthorizationDenied("Not authorized")
}

case class AuthorizationDenied(override val msg: String) extends AuthorizationError


