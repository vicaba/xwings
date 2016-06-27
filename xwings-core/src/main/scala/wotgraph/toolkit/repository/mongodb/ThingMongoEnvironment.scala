package wotgraph.toolkit.repository.mongodb

import com.typesafe.config.Config
import reactivemongo.api.{DB, MongoConnectionOptions, MongoDriver, ScramSha1Authentication}
import reactivemongo.core.nodeset.Authenticate

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration._

case class ThingMongoEnvironment(config: Config) extends MongoEnvironment {
  override lazy val driver: MongoDriver = MongoDriver()

  val dbName = config.getString("mongodb.db")
  val dbUser = config.getString("mongodb.user")
  val dbPassword = config.getString("mongodb.password")

  val credentials = Seq(Authenticate(dbName, dbUser, dbPassword))

  override lazy val db: DB = {
    val connection = driver.connection(
      config.getStringList("mongodb.servers").asScala.toList,
      MongoConnectionOptions(authMode = ScramSha1Authentication),
      credentials
    )
    // TODO: Remove synchronization code smell!
    Await.result(connection.database(config.getString("mongodb.db")), 3.seconds)
  }
}
