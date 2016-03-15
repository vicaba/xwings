package infrastructure.thing.repository

import java.nio.ByteBuffer
import java.util.{UUID, Base64}
import application.Thing
import com.google.common.base.Strings
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WS, WSAuthScheme, WSClient}
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait ThingRepository {

  def getThing(id: UUID): Future[Thing]

  def getThingInfo(id: UUID): Future[List[Thing]]

  def getThingActions(id: UUID): Future[List[Thing]]

  def getThingRelations(id: UUID): Future[Strings]

  def getThingAndChildren(id: UUID, depth: Int = 1): Future[List[Thing]]

  def getThingChildren(id: UUID): Future[List[Thing]]

}


class ThingRepositoryForNeo4j(wsClient: WSClient) {


}


object Main {

  def createRequestForNeo4j(wsClient: WSClient): WSRequest = {
    val request = wsClient
      .url("http://192.168.44.13:7474/db/data/transaction/commit")
      .withHeaders(
        "Accept" -> "application/json; charset=UTF-8"
        , "Content-Type" -> "application/json")
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
