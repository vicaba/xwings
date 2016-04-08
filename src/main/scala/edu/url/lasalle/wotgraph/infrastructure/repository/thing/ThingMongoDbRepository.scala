package edu.url.lasalle.wotgraph.infrastructure.repository.thing

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.thing.Thing
import edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.MongoCRUDService
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.Implicits._
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.ThingSerializer
import play.api.libs.json.Json
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.DB
import play.modules.reactivemongo.json._
import reactivemongo.bson.BSONDocument
// BSON implementation of the count command
import reactivemongo.api.commands.bson.BSONCountCommand.{ Count, CountResult }

// BSON serialization-deserialization for the count arguments and result
//import reactivemongo.api.commands.bson.BSONCountCommandImplicits._

import scala.concurrent.{ExecutionContext, Future}


case class ThingMongoDbRepository(db: DB)(implicit ec: ExecutionContext) extends MongoCRUDService[Thing, UUID] {

  override val collection: JSONCollection = db.collection("thing")

  override def getIdFromEntity(entity: Thing): UUID = entity._id

  override val identityName: String = "_id"

}
