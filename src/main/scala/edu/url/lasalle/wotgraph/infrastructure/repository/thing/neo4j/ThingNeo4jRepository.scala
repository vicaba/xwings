package edu.url.lasalle.wotgraph.infrastructure.repository.thing.neo4j

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.thing.Thing
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.Neo4jConf.Config
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.helpers.Neo4jOGMHelper
import org.neo4j.ogm.cypher.Filter

import scala.concurrent.{ExecutionContext, Future}

case class ThingNeo4jRepository(
                                 override val neo4jConf: Config
                               )(implicit ec: ExecutionContext)
  extends Neo4jOGMHelper {

  def findById(id: UUID): Future[Option[Thing]] = {

    val session = getSession()

    Future {
      collectionToList(session.loadAll(classOf[Neo4jThing], new Filter("_id", id.toString)))
        .headOption.map(Neo4jThingHelper.neo4jThingAsThingView)
    }

  }

  def create(t: Thing): Future[Thing] = {

    val session = getSession()

    Future {
      session.save[Neo4jThing](Neo4jThingHelper.thingAsNeo4jThingView(t))
      t
    }
  }
}
