package wotgraph.toolkit.scalactic

import org.scalactic.{Every, Many, One}

object ErrorHelper {

  def every2List[T](every: Every[T]): List[T] = every match {
    case m: Many[T] => many2List(m)
    case One(o) => List(o)
  }

  def many2List[T](many: Many[T]): List[T] =
    List[T](many.firstElement, many.secondElement) ::: many.otherElements.toList

}
