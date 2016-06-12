package wotgraph.app.role.domain.entity

import java.util.UUID

import wotgraph.app.permission.domain.entity.Permission

case class Role(id: UUID = UUID.randomUUID(), name: String, permissions: Set[Permission] = Set.empty)


