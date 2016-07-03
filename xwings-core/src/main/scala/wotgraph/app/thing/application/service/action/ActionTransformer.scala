package wotgraph.app.thing.application.service.action

import java.util.UUID

import wotgraph.app.thing.domain.entity.Action

import scala.annotation.tailrec

trait ActionTransformer extends (ThingAndAction => List[Action]) {

  def transform(ta: ThingAndAction): List[Action]

  override def apply(ta: ThingAndAction): List[Action] = transform(ta)

}

object ActionTransformer {

  /**
    * Given initialActions produces a new augmented List of [[Action]] applying all transformers
    *
    * @param initialActions
    * @param transformers
    * @return
    */
  def transform(
                 initialActions: List[ThingAndAction],
                 transformers: List[ActionTransformer]
               ): List[Action] =
    transform(initialActions, Nil, transformers.iterator)

  /**
    * Given initialActions produces a new augmented List of [[Action]] applying all transformers
    *
    * @param thingId
    * @param initialActions
    * @param transformers
    * @return
    */
  def transform(
                 thingId: UUID,
                 initialActions: List[Action],
                 transformers: List[ActionTransformer]
               ): List[Action] = {
    transform(initialActions.map(ThingAndAction(thingId, _)), transformers)
  }

  /**
    * Given initialActions produces a new augmented List of [[Action]] applying all transformers
    *
    * @param initialActions the actions to augment
    * @param acc  an accumulator used for accumulating the augmented actions
    * @param transformers the action transformers that produce or replace new actions from a single action
    * @return a transformed or augmented List of [[Action]]
    */
  @tailrec private def transform(
                          initialActions: List[ThingAndAction],
                          acc: List[Action],
                          transformers: Iterator[ActionTransformer]
                        ): List[Action] = {

    val at = transformers.next()
    val accumulated = initialActions.flatMap(at) ::: acc

    if (transformers.hasNext) transform(initialActions, accumulated, transformers)
    else accumulated

  }
}
