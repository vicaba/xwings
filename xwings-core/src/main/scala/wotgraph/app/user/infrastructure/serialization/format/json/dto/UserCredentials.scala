package wotgraph.app.user.infrastructure.serialization.format.json.dto

import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, _}
import wotgraph.app.user.application.usecase.dto.{CreateUser, UserCredentials}
import wotgraph.app.user.infrastructure.serialization.keys.UserKeys._

object UserCredentialsSerializer {

  val userCredentialsReads: Reads[UserCredentials] = (
    (__ \ Name).read[String] and
      (__ \ Password).read[String]
    ) (UserCredentials.apply _)

}
