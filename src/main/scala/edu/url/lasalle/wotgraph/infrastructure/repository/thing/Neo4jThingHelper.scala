package edu.url.lasalle.wotgraph.infrastructure.repository.thing

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.thing.Thing

import scala.collection.JavaConverters._

object Neo4jThingHelper {

  implicit def thingAsNeo4jThingView(t: Thing): Neo4jThing = {

    /*    if (t.children.isEmpty) {
          new Neo4jThing(t._id.toString, Set.empty[Neo4jThing].asJava, t.id)
        } else {
          for {
            e <- t.children
          } yield {
            thingAsNeo4jThingView(e)
          }
        }*/

    val childrenSet = if (t.children.nonEmpty) {
      for {
        e <- t.children
      } yield {
        thingAsNeo4jThingView(e)
      }
    } else {
      Set.empty[Neo4jThing]
    }

    new Neo4jThing(t._id.toString, childrenSet.asJava, t.id)
  }

  implicit def neo4jThingAsThingView(t: Neo4jThing): Thing = {

    val childrenSetAsScala = t.children.asScala.toSet

    val childrenSet: Set[Thing] = if (childrenSetAsScala.nonEmpty) {
      for {
        e <- childrenSetAsScala
      } yield {
        neo4jThingAsThingView(e)
      }
    } else {
      Set.empty[Thing]
    }

    Thing(UUID.fromString(t._id), None, Set.empty, childrenSet, t.getNeo4jId)

  }

}
