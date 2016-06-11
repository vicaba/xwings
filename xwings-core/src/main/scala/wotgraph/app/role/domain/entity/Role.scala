package wotgraph.app.role.domain.entity

import java.util.UUID

import wotgraph.app.permission.domain.entity.Permission

object Role {

  object Keys {

    val IdKey = "_id"

    val NameKey = "name"

  }

}

case class Role(id: UUID = UUID.randomUUID(), name: String, permissions: Set[Permission] = Set.empty)


