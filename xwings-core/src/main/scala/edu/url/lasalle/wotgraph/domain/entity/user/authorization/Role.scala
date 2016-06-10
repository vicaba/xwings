package edu.url.lasalle.wotgraph.domain.entity.user.authorization

import java.util.UUID

object Role {

  object Keys {

    val IdKey = "_id"

    val NameKey = "name"

  }

}

case class Role(id: UUID = UUID.randomUUID(), name: String, permissions: Set[Permission] = Set.empty)


