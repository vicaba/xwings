package wotgraph.app.thing.infrastructure.repository.neo4j

import java.util.UUID

import wotgraph.app.thing.domain.entity.Thing
import wotgraph.toolkit.repository.neo4j.entity.Neo4jThing

import scala.collection.JavaConverters._

object Neo4jThingHelper {

  implicit def thingAsNeo4jThingView(t: Thing): Neo4jThing =
    new Neo4jThing(t._id.toString, t.children.map(thingAsNeo4jThingView).asJava, t.id)

  implicit def neo4jThingAsThingView(t: Neo4jThing): Thing =
    Thing(UUID.fromString(t._id), None, Set.empty, t.children.asScala.toSet.map(neo4jThingAsThingView), t.getNeo4jId)
}
