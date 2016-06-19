package bootstrap

import java.util.UUID

import org.apache.commons.codec.binary.Hex
import org.scalactic.{Every, Or}
import wotgraph.app.thing.domain.entity.{Action, Metadata, Thing}
import wotgraph.app.thing.domain.repository.ThingRepository
import wotgraph.app.thing.domain.service.ContextProvider
import wotgraph.toolkit.DependencyInjector._
import play.api.libs.json.{JsObject, Json}
import scaldi.Injectable._
import wotgraph.app.authorization.application.service.AuthorizationService
import wotgraph.app.error.AppError
import wotgraph.app.permission.application.usecase.{ListPermissionsUseCase, PermissionUseCasePermissionProvider}
import wotgraph.app.permission.domain.entity.Permission
import wotgraph.app.permission.infrastructure.repository.neo4j.PermissionNeo4jRepository
import wotgraph.app.role.application.usecase.{CreateRoleUseCase, ListRolesUseCase, RoleUseCasePermissionProvider}
import wotgraph.app.role.domain.entity.Role
import wotgraph.app.role.infrastructure.repository.neo4j.RoleNeo4jRepository
import wotgraph.app.thing.application.usecase.{ListThingsUseCase, ThingUseCasePermissionProvider}
import wotgraph.app.user.application.usecase._
import wotgraph.app.user.application.usecase.dto.CreateUser
import wotgraph.app.user.domain.entity.User
import wotgraph.app.user.infrastructure.repository.neo4j.UserNeo4jRepository
import wotgraph.toolkit.crypt.{Hasher, PBKDF2WithHmacSHA512}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}


object ThingHelper {

  def createThing(identifier: Int): Thing = {

    val id = UUID.randomUUID()
    val contextValue = Map("httpMethod" -> "GET", "url" -> "https://es.wikipedia.org/wiki/Wikipedia:Portada")
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
      Await.result(f1, 6.seconds)
      val f2 = repo.create(t3WithChildren)
      Await.result(f2, 6.seconds)
      val f3 = repo.create(t2WithChildren)
      Await.result(f3, 6.seconds)
      val f4 = repo.create(tWithChildren)
      Await.result(f4, 6.seconds)
    }
  }

  def deleteNodes() = repo.deleteAll()

}

object PermissionHelper {

  val repo: PermissionNeo4jRepository = inject[PermissionNeo4jRepository](identified by 'PermissionNeo4jRepository)

  def createNodes(): Future[List[Permission]] = {

    val perms =
      (ThingUseCasePermissionProvider ::
        RoleUseCasePermissionProvider ::
        PermissionUseCasePermissionProvider ::
        UserUseCasePermissionProvider ::
        Nil).flatMap(_.permissions)

    Future.sequence(perms.map(repo.create))

  }

  def getAll = repo.getAll

  def deleteNodes() = repo.deleteAll()

}

object RoleHelper {

  val Admin = "admin"

  val Registered = "registered"

  val Freemium = "freemium"

  val repo: RoleNeo4jRepository = inject[RoleNeo4jRepository](identified by 'RoleNeo4jRepository)

  def createNodes(perms: List[Permission]): Future[List[Role]] = {

    val adminOnlyPerms =
      (UserUseCasePermissionProvider ::
        RoleUseCasePermissionProvider ::
        PermissionUseCasePermissionProvider ::
        Nil).flatMap(_.permissions).toSet

    val roles =
      Role(name = Admin, permissions = perms.toSet) ::
        Role(name = Registered, permissions = perms.toSet -- adminOnlyPerms) ::
        Role(name = Freemium, permissions = (ListThingsUseCase :: Nil).map(_.permission).toSet) ::
        Nil

    Future.sequence(roles.map(repo.create))

  }

  def deleteNodes() = repo.deleteAll

}

object UserHelper {

  val repo: UserNeo4jRepository = inject[UserNeo4jRepository](identified by 'UserNeo4jRepository)

  val updateUseCase = inject[UpdateUserUseCase](identified by 'UpdateUserUseCase)

  def createNodes(roles: List[Role]) = {

    val useCase = inject[CreateUserUseCase](identified by 'CreateUserUseCase)


    val users =
      CreateUser("Xavi", "xavier", roles.filter(_.name == RoleHelper.Admin).head.id) ::
        CreateUser("Vic", "victor", roles.filter(_.name == RoleHelper.Registered).head.id) ::
        CreateUser("Nadia", "nadie", roles.filter(_.name == RoleHelper.Freemium).head.id) ::
        Nil

    Future.sequence(users.map(useCase.execute))

  }

  def getAll = repo.getAll

  def deleteNodes() = repo.deleteAll

}

object AuthorizationHelper {
  val service = inject[AuthorizationService](identified by 'AuthorizationService)
}

object Bootstrap {

  def main(args: Array[String]) {

    ThingHelper.deleteNodes()
    UserHelper.deleteNodes()
    RoleHelper.deleteNodes()
    PermissionHelper.deleteNodes()



    ThingHelper.createNodes()
    val f = PermissionHelper.createNodes()

    val f2 = f.flatMap(RoleHelper.createNodes)

    val f3 = f2.flatMap { r =>
      UserHelper.createNodes(r)
    } recover {
      case e: Throwable => println(e)
    }

    Await.ready(f3, Duration.Inf)

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

    val f3 = UserHelper.getAll

    println("Hola")

    Await.ready(f3, Duration.Inf)

    //val f4 = UserHelper.repo.delete(UUID.fromString("395e11a7-79e6-40b0-8e33-0388b4f9c586"))

    //Await.ready(f4, Duration.Inf)

    val f5 = UserHelper.updateUseCase.execute("55eec6fe-6a64-4b31-b671-25bb5cb0157f", CreateUser("name", "pass", UUID.fromString("ece51b67-158c-411c-9aeb-a90376462389")))

    Await.ready(f5, Duration.Inf)

    val f6 = AuthorizationHelper.service.execute(
      UUID.fromString("14a67dd4-6d31-44ac-9d39-f8b7a8b17bbd"),
      UUID.fromString("a2e0c04b-3507-4daa-a783-55d0763306c4")
    )

    f6.map { r =>
      // true
      println("f6" + r)
    } recover {
      case e: Throwable => e.getStackTrace.foreach(println); throw e
    }

    Await.ready(f6, Duration.Inf)

    val f7 = AuthorizationHelper.service.execute(
      UUID.fromString("bb56599d-d82f-458d-993b-f7c0c993c56c"),
      UUID.fromString("a2e0c04b-3507-4daa-a783-55d0763306c4")
    )

    f7.map { r =>
      // false
      println(r)
    }

    Await.ready(f7, Duration.Inf)

  }

}
