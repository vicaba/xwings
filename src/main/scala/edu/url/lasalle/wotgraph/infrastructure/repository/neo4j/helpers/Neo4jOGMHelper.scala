package edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.helpers

import java.util

import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.Neo4jConf
import org.neo4j.ogm.session.SessionFactory

import scala.annotation.tailrec

trait Neo4jOGMHelper {

  val neo4jConf: Neo4jConf.Config

  lazy val session: org.neo4j.ogm.session.Session = {
    val sessionFactory = new SessionFactory(neo4jConf.sessionConfig, neo4jConf.packages:_*)
    sessionFactory.openSession()
  }

  def iterableToList[T](iterable: java.lang.Iterable[T]): List[T] = {

    @tailrec def iteratorToList[T](iterator: java.util.Iterator[T], list: List[T]): List[T] = {
      if (iterator.hasNext) {
        val elem = iterator.next()
        iteratorToList(iterator, list.+:(elem))
      } else list
    }

    iteratorToList(iterable.iterator(), List.empty)
  }
}
