package wotgraph.app.thing.application.service.action

import java.util.UUID

import wotgraph.app.thing.domain.entity.Action

case class ThingAndAction(thingId: UUID, action: Action)


trait ActionTransformer extends PartialFunction[ThingAndAction, List[Action]] {

  val contextId: UUID

  private var result: List[ThingAndAction] = Nil

  private var produced = false

  protected[action] def aroundTransform(ta: ThingAndAction): List[Action] = {
    if (ta.action.contextId != contextId)
      Nil
    else
      transform(ta)
  }

  def transform(ta: ThingAndAction): List[Action]

  @throws[Exception]
  override def apply(v1: ThingAndAction): List[Action] = aroundTransform(v1)

  override def isDefinedAt(x: ThingAndAction): Boolean = aroundTransform(x).nonEmpty
}
