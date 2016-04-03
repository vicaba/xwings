package edu.url.lasalle.wotgraph.infrastructure.repository.metadata

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.thing.Metadata
import edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.MongoCRUDService
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.DB
import edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.serializers.MetadataSerializer
import edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.serializers.MetadataSerializer.implicitMetadataFormat

import scala.concurrent.ExecutionContext

case class MetadataMongoRepository(db: DB)(implicit ec: ExecutionContext) extends MongoCRUDService[Metadata, UUID] {

  override val collection: JSONCollection = db.collection("metadata")

  override val identityName: String = MetadataSerializer.MongoIdKey

  override def getIdFromEntity(entity: Metadata): UUID = entity.thingId
}
