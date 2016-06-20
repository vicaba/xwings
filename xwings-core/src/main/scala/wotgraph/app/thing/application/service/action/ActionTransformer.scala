package wotgraph.app.thing.application.service.action

import java.util.UUID

import wotgraph.app.thing.domain.entity.Action
import wotgraph.app.thing.infrastructure.service.action.AvailableContexts


trait ActionTransformer extends PartialFunction[Action, List[Action]] {

  val contextId: UUID

  private var result: List[Action] = Nil

  private var produced = false

  protected[action] def aroundTransform(a: Action): List[Action] = {
    if (a.contextId != contextId)
      Nil
    else
      transform(a)
  }

  def transform(a: Action): List[Action]

  @throws[Exception]
  override def apply(v1: Action): List[Action] = aroundTransform(v1)

  override def isDefinedAt(x: Action): Boolean = aroundTransform(x).nonEmpty
}
