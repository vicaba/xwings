package edu.url.lasalle.wotgraph.infrastructure.repository.mongodb
import com.typesafe.config.Config
import reactivemongo.api.{DB, MongoConnectionOptions, MongoDriver}
import scala.collection.JavaConversions._

case class MongoEnvironmentImpl(config: Config) extends MongoEnvironment{
  override lazy val driver: MongoDriver = MongoDriver()
  override lazy val db: DB = {
    val connection = driver.connection(config.getStringList("mongodb.server"), MongoConnectionOptions())
    connection.db(config.getString("mongodb.db"))
  }
}
