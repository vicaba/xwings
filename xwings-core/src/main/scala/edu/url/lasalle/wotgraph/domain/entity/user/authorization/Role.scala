package edu.url.lasalle.wotgraph.domain.entity.user.authorization

import java.util.UUID

case class Role(id: UUID = UUID.randomUUID(), name: String, permissions: List[Permission] = Nil)


