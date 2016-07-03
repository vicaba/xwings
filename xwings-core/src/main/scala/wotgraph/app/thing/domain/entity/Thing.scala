package wotgraph.app.thing.domain.entity

import java.util.UUID

import org.scalactic._
import wotgraph.app.error.{AppError, Validation}

case class Thing(
                  _id: UUID = UUID.randomUUID(),
                  metadata: Option[Metadata] = None,
                  actions: Set[Action] = Set.empty,
                  children: Set[Thing] = Set.empty,
                  id: Long = -1) {

  override def equals(arg0: Any): Boolean = {
    arg0 match {
      case t: Thing => t._id == _id
      case _ => false
    }
  }

  override def hashCode: Int = {
    _id.hashCode
  }

}

object Thing {

  def ensureCorrect(t: Thing): Thing Or Every[AppError] = {
    val actionNames = t.actions.map(_.actionName)
    val groupedNames = actionNames.groupBy(identity)

    if (groupedNames.keys.count(_ => true) == actionNames.count(_ => true))
      Good(t)
    else
      Bad(One(Validation("Actions cannot have the same name")))
  }

}


