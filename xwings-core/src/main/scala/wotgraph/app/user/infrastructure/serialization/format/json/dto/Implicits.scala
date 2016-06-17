package wotgraph.app.user.infrastructure.serialization.format.json.dto

object Implicits {

  implicit val createUserJsonSerializer = CreateUserSerializer.createUserReads

}
