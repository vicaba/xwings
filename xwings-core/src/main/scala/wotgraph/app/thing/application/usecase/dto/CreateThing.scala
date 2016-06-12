package wotgraph.app.thing.application.usecase.dto

import java.util.UUID

import wotgraph.app.thing.domain.entity.{Action, Metadata, Thing}

object CreateThing {

  def toThing(c: CreateThing): Thing = {

    val metadata = Some(c.metadata)

    val children = c.children.map(Thing(_))

    val actions = c.actions

    Thing(metadata = metadata, actions = actions, children = children)
  }

}

case class CreateThing(metadata: Metadata, actions: Set[Action] = Set.empty, children: Set[UUID] = Set.empty)