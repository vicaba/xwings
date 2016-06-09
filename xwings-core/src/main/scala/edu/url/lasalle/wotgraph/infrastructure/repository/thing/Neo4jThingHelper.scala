package edu.url.lasalle.wotgraph.infrastructure.repository.thing

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.entity.thing.Thing
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.entity.Neo4jThing

import scala.collection.JavaConverters._

object Neo4jThingHelper {

  implicit def thingAsNeo4jThingView(t: Thing): Neo4jThing =
    new Neo4jThing(t._id.toString, t.children.map(thingAsNeo4jThingView).asJava, t.id)

  implicit def neo4jThingAsThingView(t: Neo4jThing): Thing =
    Thing(UUID.fromString(t._id), None, Set.empty, t.children.asScala.toSet.map(neo4jThingAsThingView), t.getNeo4jId)
}
