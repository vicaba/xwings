package edu.url.lasalle.wotgraph.infrastructure.repository.thing.mongodb

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.thing.Thing
import edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.MongoCRUDService
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.DB
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.Implicits._

import scala.concurrent.ExecutionContext

case class ThingMongoDbRepository(db: DB)(implicit ec: ExecutionContext) extends MongoCRUDService[Thing, UUID] {

  override val collection: JSONCollection = db.collection("thing")

  override def getIdFromEntity(entity: Thing): UUID = entity._id

  override val identityName: String = "_id"
}
