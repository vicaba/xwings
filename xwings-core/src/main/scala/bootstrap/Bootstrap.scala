package bootstrap

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.entity.thing.action.ContextProvider
import edu.url.lasalle.wotgraph.domain.entity.thing.{Action, Metadata, Thing}
import edu.url.lasalle.wotgraph.domain.entity.user.authorization.Permission
import edu.url.lasalle.wotgraph.domain.repository.thing.ThingRepository
import edu.url.lasalle.wotgraph.infrastructure.DependencyInjector._
import edu.url.lasalle.wotgraph.infrastructure.repository.permission.PermissionNeo4jRepository
import play.api.libs.json.{JsObject, Json}
import scaldi.Injectable._

import scala.concurrent.{Await, Future}


object ThingHelper {

  def createThing(identifier: Int): Thing = {

    val id = UUID.randomUUID()
    val contextValue = Map("httpMethod"-> "GET", "url" -> "https://es.wikipedia.org/wiki/Wikipedia:Portada")
    val actions = Set(
      Action(
        "getConsume", UUID.fromString(ContextProvider.HTTP_CONTEXT), Json.toJson(contextValue).as[JsObject].toString()
      )
    )
    val metadata = Json.parse("""{"position":{"type":"Feature","geometry":{"type":"Point","coordinates":[42.6,32.1]},"properties":{"name":"Dinagat Islands"}},"ip":"192.168.22.19"}""")
    val t = new Thing(id, Some(Metadata(metadata.as[JsObject])), actions)

    t

  }

  implicit val ec = scala.concurrent.ExecutionContext.global
  import scala.concurrent.duration._

  val repo: ThingRepository = inject[ThingRepository](identified by 'ThingRepository)

  def createNodes() = {
    var i = 0
    while (i < 1) {
      i = i + 1
      val t = createThing(1)
      val t2 = createThing(2)
      val t3 = createThing(3)
      val t4 = createThing(4)
      val tWithChildren = t.copy(children = Set(t2))
      val t2WithChildren = t2.copy(children = Set(t3))
      val t3WithChildren = t3.copy(children = Set(t4))

      val f1 = repo.create(t4)
      Await.result(f1, 3.seconds)
      val f2 = repo.create(t3WithChildren)
      Await.result(f2, 3.seconds)
      val f3 = repo.create(t2WithChildren)
      Await.result(f3, 3.seconds)
      val f4 = repo.create(tWithChildren)
      Await.result(f4, 3.seconds)
    }
  }

  def deleteNodes() = repo.deleteAll()

}

object PermissionHelper {

  implicit val ec = scala.concurrent.ExecutionContext.global

  val repo: PermissionNeo4jRepository = inject[PermissionNeo4jRepository](identified by 'PermissionNeo4jRepository)

  def createNodes(): Future[List[Permission]] = {

    val p = Permission(desc = "a perm")

    repo.create(p).map(_ => List(p))
  }

  def deleteNodes() = repo.deleteAll()

}

object Bootstrap {

  implicit val ec = scala.concurrent.ExecutionContext.global

  def main(args: Array[String]) {

    ThingHelper.deleteNodes()
    PermissionHelper.deleteNodes()

    ThingHelper.createNodes()
    val f = PermissionHelper.createNodes()

    f.map { l =>
      PermissionHelper.repo.update(l.head.copy(desc = "hola"))
    }

  }

}
