package infrastructure.thing.repository.neo4j

import java.util.UUID

import application.Thing
import domain.thing.repository.ThingRepository
import play.api.libs.json.Json
import play.api.libs.ws.ning.NingWSClient
import play.api.libs.ws.{WSAuthScheme, WSClient, WSRequest}

import infrastructure.repository.neo4j._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Neo4jWebServiceRepository {

  val wsClient: WSClient

  val domain: String
  val httpProtocol: String

  val user: String
  val password: String

  val request = createRequestForNeo4j()

  def createRequestForNeo4j(wsClient: WSClient = this.wsClient): WSRequest = {
    val request = wsClient
      .url(s"$httpProtocol://$domain/db/data/transaction/commit")
      .withHeaders(
        "Accept" -> "edu/url/lasalle/wotgraph/application/json; charset=UTF-8"
        , "Content-Type" -> "edu/url/lasalle/wotgraph/application/json")
      .withAuth(user, password, WSAuthScheme.BASIC)
    request
  }

  def getAllNodes(): WSRequest = {
    val query =
      Json.obj(
        "statements" -> List(
          Json.obj(
            "statement" -> "MATCH (n)-[r]->() RETURN n,r"
            , "resultDataContents" -> List("row", "graph")
          )))

    request
      .withMethod("POST")
      .withBody(query)
  }

}

case class ThingNeo4jWebServiceRepository(
                                           client: WSClient
                                         )
  extends Neo4jWebServiceRepository
    with ThingRepository
{

  import Neo4jHelper._

  override val wsClient: WSClient = client
  override val domain: String = "192.168..."
  override val httpProtocol: String = "http"
  override val user: String = "neo4j"
  override val password: String = "xneo4j"


  object Label {
    val Thing = "Thing"
  }

  override def getThingAndChildren(id: UUID, depth: Int = 1): Future[List[Thing]] = {
    val labels = List(Label.Thing).toLabels
    val query =
      Json.obj(
        "statements" -> List(
          Json.obj(
            "statement" -> s"MATCH (n:${labels} ({id: $id}))-[r]->() RETURN n,r"
            , "resultDataContents" -> List("row", "graph")
          )))

    request
      .withMethod("POST")
      .withBody(query)
  }

  override def getThingInfo(id: UUID): Future[List[Thing]] = ???

  override def getThingChildren(id: UUID): Future[List[Thing]] = ???

  override def getThingRelations(id: UUID): Future[String] = ???

  override def getThingActions(id: UUID): Future[List[Thing]] = ???

  override def getThing(id: UUID): Future[Option[Thing]] = ???
}

class ThingRepositoryForNeo4j(wsClient: WSClient) {


}


object Main {

  def createRequestForNeo4j(wsClient: WSClient): WSRequest = {
    val request = wsClient
      .url("http://192.168.44.13:7474/db/data/transaction/commit")
      .withHeaders(
        "Accept" -> "edu/url/lasalle/wotgraph/application/json; charset=UTF-8"
        , "Content-Type" -> "edu/url/lasalle/wotgraph/application/json")
      .withAuth("neo4j", "xneo4j", WSAuthScheme.BASIC)
    request
  }

  def createNodeTest(request: WSRequest): WSRequest = {
    val json =
      Json.obj(
        "statements" -> List(
          Json.obj(
            "statement" -> "CREATE (n) RETURN n"
            , "resultDataContents" -> List("graph")
          )))

    request
      .withMethod("POST")
      .withBody(json)
  }

  def getAllNodes(request: WSRequest): WSRequest = {
    val json =
      Json.obj(
        "statements" -> List(
          Json.obj(
            "statement" -> "MATCH (n)-[r]->() RETURN n,r"
            , "resultDataContents" -> List("row", "graph")
          )))

    request
      .withMethod("POST")
      .withBody(json)
  }

  def main(args: Array[String]) {
    val wsClient = NingWSClient()

    val f = (createRequestForNeo4j _).andThen(getAllNodes)(wsClient).execute()

    f.map(r => println(r.body))

  }
}
