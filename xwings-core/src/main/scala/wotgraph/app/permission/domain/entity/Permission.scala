package wotgraph.app.permission.domain.entity

import java.util.UUID

object Permission {

  object Keys {

    val IdKey = "_id"

    val DescKey = "desc"

  }

}

case class Permission(id: UUID = UUID.randomUUID(), desc: String)
