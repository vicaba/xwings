package wotgraph.app.permission.domain.entity

import java.util.UUID

case class Permission(id: UUID = UUID.randomUUID(), desc: String = "")
