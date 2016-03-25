package infrastructure.thing.repository.neo4j

import java.util
import java.util.UUID

import edu.url.lasalle.wotgraph.infrastructure.AppConfig
import edu.url.lasalle.wotgraph.infrastructure.thing.repository.neo4j.mappings.Thing
import org.neo4j.ogm.cypher.Filter
import org.neo4j.ogm.session.SessionFactory
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

  def createRelationTest(relation: String, request: WSRequest, ids: (UUID, UUID)): WSRequest = {
    val (id1, id2) = ids
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

  def getSession(): org.neo4j.ogm.session.Session = {
    val sessionFactory = new SessionFactory("edu.url.lasalle.wotgraph.infrastructure.thing.repository.neo4j.mappings")
    sessionFactory.openSession(s"http://${AppConfig.defaultConf.getString("neo4j.server")}", "neo4j", "xneo4j")
  }

  def getSomeThings(): util.Collection[Thing] = {
    val session = getSession()
    session.loadAll(classOf[Thing], new Filter("_id", "87ffdcc2-c28c-434f-9f4f-bc3ac0da21b3"))
  }

  def testInit() = {
    val wsClient = NingWSClient()

    val list = ListBuffer[UUID]()
    val request = createRequestForNeo4j(wsClient)
    (1 to 15).map(createNodeTest(request, _, list).execute())
    println(list)
    createRelationTest("CHILD", request, (list.head, list.last)).execute()
    createRelationTest("ACTION", request, (list.head, list(list.length - 2))).execute()
  }

  def main(args: Array[String]) {
//    testInit()

    val things = getSomeThings().toArray()
    for (t <- things) {
      println(t.asInstanceOf[Thing].actions)
    }
  }
}
