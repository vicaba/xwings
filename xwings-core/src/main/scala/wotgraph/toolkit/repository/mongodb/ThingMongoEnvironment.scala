package wotgraph.toolkit.repository.mongodb
import com.typesafe.config.Config
import reactivemongo.api.{DB, MongoConnectionOptions, MongoDriver}
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global

case class ThingMongoEnvironment(config: Config) extends MongoEnvironment{
  override lazy val driver: MongoDriver = MongoDriver()
  override lazy val db: DB = {
    val connection = driver.connection(config.getStringList("mongodb.servers"), MongoConnectionOptions())
    connection.db(config.getString("mongodb.db"))
  }
}
