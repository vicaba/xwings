package edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.helpers


import java.util

import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.Neo4jConf
import org.neo4j.ogm.session.{Session, SessionFactory}
import org.neo4j.ogm.model.Result
import scala.collection.JavaConverters._


import scala.annotation.tailrec

object Neo4jOGMHelper {

  def getSession(config: Neo4jConf.Config): Session = {
    val sessionFactory = new SessionFactory(config.sessionConfig, config.packages:_*)
    sessionFactory.openSession()
  }

}

trait Neo4jOGMHelper {

  protected def createEmptyMap = new util.HashMap[String, Object]

  protected def resultCollectionAsScalaCollection(neo4jResult: Result) = neo4jResult.queryResults().asScala.map(_.asScala)

  protected def iterableToList[T](iterable: java.lang.Iterable[T]): List[T] = {

    @tailrec def iteratorToList[T](iterator: java.util.Iterator[T], list: List[T]): List[T] = {
      if (iterator.hasNext) {
        val elem = iterator.next()
        iteratorToList(iterator, list.+:(elem))
      } else list
    }

    iteratorToList(iterable.iterator(), List.empty)
  }
}
