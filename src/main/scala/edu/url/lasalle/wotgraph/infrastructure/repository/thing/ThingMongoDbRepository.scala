package edu.url.lasalle.wotgraph.infrastructure.repository.thing

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.thing.Thing
import edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.MongoCRUDService
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.Implicits._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.DB

import scala.concurrent.ExecutionContext

/**
  * Created by vicaba on 07/04/16.
  */
case class ThingMongoDbRepository(db: DB)(implicit ec: ExecutionContext) extends MongoCRUDService[Thing, UUID] {

  override val collection: JSONCollection = db.collection("thing")

  override def getIdFromEntity(entity: Thing): UUID = entity._id

  override val identityName: String = "_id"
}
