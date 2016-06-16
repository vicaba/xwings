package bootstrap

import java.util.UUID

import wotgraph.app.thing.domain.entity.{Action, Metadata, Thing}
import wotgraph.app.thing.domain.repository.ThingRepository
import wotgraph.app.thing.domain.service.ContextProvider
import wotgraph.toolkit.DependencyInjector._
import play.api.libs.json.{JsObject, Json}
import scaldi.Injectable._
import wotgraph.app.permission.domain.entity.Permission
import wotgraph.app.permission.infrastructure.repository.neo4j.PermissionNeo4jRepository
import wotgraph.app.role.domain.entity.Role
import wotgraph.app.role.infrastructure.repository.neo4j.RoleNeo4jRepository
import scala.concurrent.duration._


import scala.concurrent.ExecutionContext.Implicits.global
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

  val repo: PermissionNeo4jRepository = inject[PermissionNeo4jRepository](identified by 'PermissionNeo4jRepository)

  def createNodes(): Future[List[Permission]] = {

    val p = Permission(desc = "Create a Thing")
    val p2 = Permission(desc = "Delete a Thing")

    for {
      fp <- repo.create(p).map(_ => List(p))
      fp2 <- repo.create(p2).map(_ => List(p2))
    } yield {
      fp ::: fp2
    }

  }

  def getAll = repo.getAll

  def deleteNodes() = repo.deleteAll()

}

object RoleHelper {

  val repo: RoleNeo4jRepository = inject[RoleNeo4jRepository](identified by 'RoleNeo4jRepository)

  def createNodes(perms: List[Permission]): Future[List[Role]] = {

    val r = Role(name = "admin", permissions = perms.toSet)
    repo.create(r).map(_ => List(r))
  }

  def deleteNodes() = repo.deleteAll()

}

object Bootstrap {

  def main(args: Array[String]) {

    ThingHelper.deleteNodes()
    PermissionHelper.deleteNodes()
    RoleHelper.deleteNodes()

    ThingHelper.createNodes()
    val f = PermissionHelper.createNodes()

    f.map(RoleHelper.createNodes)


  }

}

object Query {

  def main(args: Array[String]) {
    val f = RoleHelper.repo.findById(UUID.fromString("76b0cfe5-07fc-4c23-a855-72e8088f6222"))

    f.map { r =>

      println(r)

    }

    Await.ready(f, Duration.Inf)

    val f2 = PermissionHelper.getAll

    println("Hola")

    Await.ready(f2, Duration.Inf)


  }

}
