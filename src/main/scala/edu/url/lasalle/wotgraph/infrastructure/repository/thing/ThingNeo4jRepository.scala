package edu.url.lasalle.wotgraph.infrastructure.repository.thing

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.thing.Thing
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.Neo4jConf.Config
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.helpers.Neo4jOGMHelper
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.ThingSerializer
import org.neo4j.ogm.cypher.{BooleanOperator, Filter, Filters}

import scala.concurrent.{ExecutionContext, Future}

case class ThingNeo4jRepository(
                                 override val neo4jConf: Config
                               )(implicit ec: ExecutionContext)
  extends Neo4jOGMHelper {

  val DefaultQueryDepth = 0

  def findById(id: UUID): Future[Option[Thing]] = {

    Future {
      collectionToList(session.loadAll(classOf[Neo4jThing], new Filter(ThingSerializer.IdKey, id.toString), DefaultQueryDepth))
        .headOption.map(Neo4jThingHelper.neo4jThingAsThingView)
    }

  }

  def create(t: Thing): Future[Thing] = {

    Future {
      session.save[Neo4jThing](Neo4jThingHelper.thingAsNeo4jThingView(t))
      t
    }
  }

  def getThings(o: Iterable[Thing]): Future[Iterable[Thing]] = {

    def idFilterProducer(id: UUID) = new Filter(ThingSerializer.IdKey, id.toString)

    val filterList = o.map { t =>
      val filter = idFilterProducer(t._id)
      filter.setBooleanOperator(BooleanOperator.OR)
      filter
    } toList

    val filters = new Filters()

    filters.add(filterList:_*)

    Future {


      val coll = collectionToList(session.loadAll(classOf[Neo4jThing], filters, DefaultQueryDepth)) map Neo4jThingHelper.neo4jThingAsThingView

      println("hola")

      coll


    }

  }
}
