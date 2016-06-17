package wotgraph.app.user.infrastructure.http.serialization.format.json

import play.api.libs.json._
import play.api.libs.json.Writes._
import play.api.libs.json.OWrites
import wotgraph.app.role.infrastructure.serialization.format.json.RoleSerializer
import wotgraph.app.user.domain.entity.User
import wotgraph.app.user.infrastructure.serialization.keys.UserKeys._

object UserMinifiedSerializer {

  private object UserWrites extends OWrites[User] {
    override def writes(o: User): JsObject =
      Json.obj(
        Id -> o.id.toString,
        Name -> o.name.toString,
        Role -> RoleSerializer.roleWrites.writes(o.role)
      )
  }


  val userWrites: OWrites[User] = UserWrites

  val userSeqWrites = Writes.seq[User](userWrites)
}
