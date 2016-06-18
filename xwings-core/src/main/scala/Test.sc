import java.util.UUID

object User {
  type UserId = UUID
}

case class User(id: User.UserId = UUID.randomUUID())