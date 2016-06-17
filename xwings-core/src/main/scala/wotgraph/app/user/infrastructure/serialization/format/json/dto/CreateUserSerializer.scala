package wotgraph.app.user.infrastructure.serialization.format.json.dto

import java.util.UUID

import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, _}
import wotgraph.app.user.application.usecase.dto.CreateUser
import wotgraph.app.user.infrastructure.serialization.keys.UserKeys._


object CreateUserSerializer {
  val createUserReads: Reads[CreateUser] = (
    (__ \ Name).read[String] and
      (__ \ Password).read[String] and
      (__ \ Role).read[UUID]
    ) (CreateUser.apply _)
}
