package wotgraph.toolkit.repository.dsl

sealed trait FieldOrdering

object FieldOrdering {
  case object Ascendant extends FieldOrdering
  case object Descendant extends FieldOrdering
}

case class Order(field: String, order: FieldOrdering)