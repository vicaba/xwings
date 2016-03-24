package edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.helpers


object Implicits {
  implicit class StringLabelImprovement(labelList: List[String]) {
    def toLabels: String = labelList.mkString(":")
  }
}
