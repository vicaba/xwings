package edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.helpers

import java.util

import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.Neo4jConf
import org.neo4j.ogm.session.SessionFactory

trait Neo4jOGMHelper {

  val neo4jConf: Neo4jConf.Config

  lazy val session: org.neo4j.ogm.session.Session = {
    val sessionFactory = new SessionFactory(neo4jConf.sessionConfig, neo4jConf.packages:_*)
    sessionFactory.openSession()
  }

  def collectionToList[T](coll: util.Collection[T]): List[T] = {
    coll.toArray.toList.asInstanceOf[List[T]]
  }
}
