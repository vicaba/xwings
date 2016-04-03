package edu.url.lasalle.wotgraph.infrastructure.repository.thing.neo4j

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.thing.Thing

import scala.collection.JavaConverters._

object Neo4jThingHelper {

  implicit def thingAsNeo4jThingView(t: Thing): Neo4jThing =
    new Neo4jThing(t._id.toString, t.hName, t.children.map(thingAsNeo4jThingView).asJava)

  implicit def neo4jThingAsThingView(t: Neo4jThing): Thing =
    Thing(UUID.fromString(t._id), t.hName, None, Set.empty, t.children.asScala.toSet.map(neo4jThingAsThingView), t.getNeo4jId)

}
