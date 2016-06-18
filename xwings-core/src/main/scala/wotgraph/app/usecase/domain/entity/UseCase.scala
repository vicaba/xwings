package wotgraph.app.usecase.domain.entity

import java.util.UUID

import wotgraph.app.permission.domain.entity.Permission

case class UseCase(id: UUID = UUID.randomUUID(), name: String = "", permissions: Set[Permission] = Set.empty)
