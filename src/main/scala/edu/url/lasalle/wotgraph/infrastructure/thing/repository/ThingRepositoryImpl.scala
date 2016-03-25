package edu.url.lasalle.wotgraph.infrastructure.thing.repository

import java.util.UUID

import application.{Action, Thing}
import domain.thing.repository.ThingRepository
import edu.url.lasalle.wotgraph.infrastructure.AppConfig
import edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.{MongoDbConfig, ThingsMongoEnvironment}
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.{Neo4jConfig, Neo4jWebServiceRepository}
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.serializers.Implicits._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.json.Reads._
import play.modules.reactivemongo.json._
import play.api.libs.ws.WSRequest
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.{ExecutionContext, Future}

case class ThingRepositoryImpl(
                                neo4jConfig: Neo4jConfig,
                                mongoDbConfig: MongoDbConfig
                              )
                              (implicit ec: ExecutionContext)
  extends ThingRepository
{

  import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.helpers.Implicits._

  val neo4jPreparedRequest: WSRequest = Neo4jWebServiceRepository.createPreparedRequest(neo4jConfig)

  val mongoDbCollection = mongoDbConfig.collection

  object Label {
    val Thing = "Thing"
    val Action = "Action"
    val Child = "Child"
  }

  private def neo4jToListOfThing(unparsedJson: String): List[Thing] = {
    println(unparsedJson)
    println()
    val jsValue = Json.parse(unparsedJson)
    val nodes = (jsValue \ "results") (0) \ "data" \\ "row"
    nodes.map(n => n(0).validate[Thing].get).toList
  }

  private def addThingLabelToThing(thing: Thing): Thing =
    if (thing.labels.contains(Label.Thing)) thing
    else thing.copy(labels = thing.labels.+:(Label.Thing))

  override def getThingGraph(id: UUID, depth: Int = 1): Future[List[Thing]] = {
    val labels = List(Label.Thing).toLabels
    val query =
      Json.obj(
        "statements" -> List(
          Json.obj(
            "statement" -> s"MATCH (n:$labels ({id: $id}))-[r*0..$depth]->(n2) RETURN n,r,n2"
            , "resultDataContents" -> List("row")
          )))

    val queryRequest = neo4jPreparedRequest
      .withMethod("POST")
      .withBody(query)
    queryRequest.execute().map(r => neo4jToListOfThing(r.body))
  }

  override def getThingInfo(id: UUID): Future[Option[JsValue]] = {
    mongoDbCollection.find(Json.obj("id" -> id)).one[JsValue]
  }

  override def getThingChildren(id: UUID): Future[List[Thing]] = {
    val relationLabels = List(Label.Child).toLabels
    val query =
      Json.obj(
        "statements" -> List(
          Json.obj(
            "statement" -> s"MATCH (n ({id: $id}))-[r:$relationLabels]->(n2) RETURN n2,r"
            , "resultDataContents" -> List("row")
          )))
    val queryRequest = neo4jPreparedRequest
      .withMethod("POST")
      .withBody(query)
    queryRequest.execute().map(r => neo4jToListOfThing(r.body))
  }

  override def getThingRelations(id: UUID): Future[String] = ???

  override def getThingActions(id: UUID): Future[List[Thing]] = {
    val relationLabels = List(Label.Action).toLabels
    val query =
      Json.obj(
        "statements" -> List(
          Json.obj(
            "statement" -> s"MATCH (n ({id: $id}))-[r:$relationLabels]->() RETURN n,r"
            , "resultDataContents" -> List("row")
          )))
    val queryRequest = neo4jPreparedRequest
      .withMethod("POST")
      .withBody(query)
    queryRequest.execute().map(r => neo4jToListOfThing(r.body))
  }

  override def getThing(id: UUID): Future[Option[Thing]] = {
    val relationLabels = List(Label.Action).toLabels
    val query =
      Json.obj(
        "statements" -> List(
          Json.obj(
            "statement" -> s"MATCH (n:Thing {_id: '$id'})-[r:${Label.Action}|:${Label.Child}]->(n2) RETURN {_id:n._id, hName:n.hName, action:n.action, relations:type(r)} AS Thing, n2"
            , "resultDataContents" -> List("row")
          )))
    val queryRequest = neo4jPreparedRequest
      .withMethod("POST")
      .withBody(query)
    queryRequest.execute().map(r => neo4jToListOfThing(r.body).headOption)
  }

  override def getAllThings(skip: Int, limit: Int): Future[List[Thing]] = {
    val labels = List(Label.Thing).toLabels
    val query =
      Json.obj(
        "statements" -> List(
          Json.obj(
            "statement" -> s"MATCH (n:$labels) OPTIONAL MATCH (n)-[r]->() RETURN n,r"
              , "resultDataContents" -> List("row")
          )))

    val queryRequest = neo4jPreparedRequest
      .withMethod("POST")
      .withBody(query)
    queryRequest.execute().map(response => neo4jToListOfThing(response.body))
  }

  override def createThing(thing: Thing): Future[Thing] = {
    val _thing =  addThingLabelToThing(thing)
    val action = actionJsonSerializer.writes(thing.action).toString
    val labels = if (_thing.labels.isEmpty) "" else ":" + _thing.labels.mkString(":")
    val json =
      Json.obj(
        "statements" -> List(
          Json.obj(
            "statement" -> s"CREATE (n$labels {_id: '${_thing.id}', hName: '${_thing.humanName}', action: '${action}'}) RETURN n"
            , "resultDataContents" -> List("graph")
          )))
    val queryRequest = neo4jPreparedRequest
      .withMethod("POST")
      .withBody(json)
    queryRequest.execute().map { r =>
      thing
    }
  }

}

object Main {

  def main(args: Array[String]) {
    val wsClient = NingWSClient()
    val conf = AppConfig.defaultConf
    val neo4jConfig = Neo4jConfig(wsClient, conf.getString("neo4j.server"), "http", "neo4j", "xneo4j")
    val mongoDbConfig = MongoDbConfig(ThingsMongoEnvironment(AppConfig.defaultConf).db.collection("metadata"))
    implicit val ec = scala.concurrent.ExecutionContext.global
    val repo = ThingRepositoryImpl(neo4jConfig, mongoDbConfig)

    //(createRequestForNeo4j _ andThen createNodesTest)(wsClient).execute().map(r => println(r))

/*    repo.createThing(
      Thing(
        humanName = "SomeThing"
        , action = Action("", UUID.randomUUID(), "")
        , labels = Nil
        , relations = Nil)
    ).map(r => println(r))*/

    //repo.getAllThings(1, 1).map(r => println(r))
    repo.getThing(UUID.fromString("3efa83e8-c5cb-4322-a188-bba2051ab580")).map(r => println(r))

  }
}

