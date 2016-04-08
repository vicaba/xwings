package edu.url.lasalle.wotgraph.domain.thing

import java.util.UUID

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


