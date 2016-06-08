package edu.url.lasalle.wotgraph.domain.entity.user.authorization

import java.util.UUID

case class Permission(id: UUID = UUID.randomUUID(), desc: String)
