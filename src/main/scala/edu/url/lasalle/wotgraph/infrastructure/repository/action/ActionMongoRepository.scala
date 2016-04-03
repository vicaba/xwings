package edu.url.lasalle.wotgraph.infrastructure.repository.action

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.repository.thing.ActionRepository
import edu.url.lasalle.wotgraph.domain.thing.Action
import edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.MongoCRUDService
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.{DB, ReadPreference}
import edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.serializers.ActionSerializer
import edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.serializers.ActionSerializer.implicitSetOfActionsFormat
import play.api.libs.json.Json

import play.api.libs.json._
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

case class ActionMongoRepository(db: DB)(implicit ec: ExecutionContext)
  extends MongoCRUDService[Set[Action], UUID]
    with ActionRepository {

  override val collection: JSONCollection = db.collection("action")

  override val identityName: String = ActionSerializer.MongoIdKey

  override def getIdFromEntity(entity: Set[Action]): UUID = entity.head.thingId

  override def getActionsForThingIds(ids: Set[UUID]): Future[List[Set[Action]]] = {

    val idsToFind = ids.map { id =>
      Json.obj(ActionSerializer.MongoIdKey -> id)
    }

    val selector = Json.obj("$or" -> idsToFind)

    collection.find(selector).cursor[Set[Action]](readPreference = ReadPreference.primary).collect[List]()
  }
}
