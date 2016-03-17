package infrastructure.repository

package object neo4j {

  object Neo4jHelper {

    implicit class StringLabelImprovement(labelList: List[String]) {
      def toLabels: String = labelList.mkString(":")
    }
  }
}
