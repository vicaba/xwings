package edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.helpers

import java.util

import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.Neo4jConf
import org.neo4j.ogm.session.SessionFactory

trait Neo4jOGMHelper {

  val neo4jconf: Neo4jConf.Config

  def getSession(): org.neo4j.ogm.session.Session = {
    val sessionFactory = new SessionFactory(neo4jconf.packages:_*)
    sessionFactory.openSession(neo4jconf.server.toString, neo4jconf.credentials.username, neo4jconf.credentials.password)
  }

  def collectionToList[T](coll: util.Collection[T]): List[T] = {
    coll.toArray.toList.asInstanceOf[List[T]]
  }
}
