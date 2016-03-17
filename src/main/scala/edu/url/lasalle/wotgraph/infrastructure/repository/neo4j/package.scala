package infrastructure.repository

package object neo4j {

  object Neo4jHelper {

    case class Label(s: String)

    implicit def stringToLabel(s: String): Label = {
      return Label(s)
    }

    implicit class StringLabelImprovement(labelList: List[String]) {
      def toLabels: String = labelList.mkString(":")
    }
  }
}
