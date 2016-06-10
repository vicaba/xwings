package edu.url.lasalle.wotgraph.domain.entity.user.authorization

import java.util.UUID

object Permission {

  object Keys {

    val IdKey = "_id"

    val DescKey = "desc"

  }

}

case class Permission(id: UUID = UUID.randomUUID(), desc: String)
