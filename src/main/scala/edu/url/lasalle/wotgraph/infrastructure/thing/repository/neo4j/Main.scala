package infrastructure.thing.repository.neo4j

import java.util.UUID

import edu.url.lasalle.wotgraph.infrastructure.AppConfig
import play.api.libs.json.Json
import play.api.libs.ws.ning.NingWSClient
import play.api.libs.ws.{WSAuthScheme, WSClient, WSRequest}

import scala.collection.mutable._

object Main {

  def createRequestForNeo4j(wsClient: WSClient): WSRequest = {
    val domain = AppConfig.defaultConf.getString("neo4j.server")
    val username = AppConfig.defaultConf.getString("neo4j.user")
    val password = AppConfig.defaultConf.getString("neo4j.password")

    val request = wsClient
      .url(s"http://$domain/db/data/transaction/commit")
      .withHeaders(
        "Accept" -> "application/json; charset=UTF-8"
        , "Content-Type" -> "application/json")
      .withAuth(username, password, WSAuthScheme.BASIC)
    request
  }

  def createNodeTest(request: WSRequest, id: Int, list: ListBuffer[UUID]): WSRequest = {
    val name = s"Thing_$id"
    val thingId = UUID.randomUUID()
    list.+=(thingId)
    val actionId = UUID.randomUUID()
    val action = s"""{"actionName":"getAction", "contextId":"$actionId", "contextValue": "graph_recursive"}"""
    val json =
      Json.obj(
        "statements" -> List(
          Json.obj(
            "statement" -> s"CREATE (n: Thing {_id: '$thingId', hName: '$name', action: '$action'}) RETURN n"
            , "resultDataContents" -> List("graph")
          )))

    println(json)

    request
      .withMethod("POST")
      .withBody(json)
  }

  def createRelationTest(request: WSRequest, list: ListBuffer[UUID]): WSRequest = {
    val relation = "CHILD"
    val id1 = list.head
    val id2 = list.last
    val json =
      Json.obj(
        "statements" -> List(
          Json.obj(
            "statement" -> s"MATCH (n),(m) WHERE n._id = '$id1' AND m._id = '$id2' CREATE (n)-[r:$relation]->(m) RETURN n,r,m"
            , "resultDataContents" -> List("graph")
          )))

    println(json)

    request
      .withMethod("POST")
      .withBody(json)
  }

  def getAllNodes(request: WSRequest): WSRequest = {
    val json =
      Json.obj(
        "statements" -> List(
          Json.obj(
            "statement" -> "MATCH (n) OPTIONAL MATCH (n)-[r]->() RETURN n,r"
            , "resultDataContents" -> List("row")
          )))

    request
      .withMethod("POST")
      .withBody(json)
  }

  def main(args: Array[String]) {
    val wsClient = NingWSClient()

//    (createRequestForNeo4j _).andThen(createNodeTest)(wsClient).execute()

    val list = ListBuffer[UUID]()
    val request = createRequestForNeo4j(wsClient)
    (1 to 15).map(createNodeTest(request,_,list).execute())
    println(list)
    createRelationTest(request,list).execute()

  }
}
