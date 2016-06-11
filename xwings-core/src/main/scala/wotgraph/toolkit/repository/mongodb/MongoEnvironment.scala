package wotgraph.toolkit.repository.mongodb

import reactivemongo.api.{DB, MongoDriver}

trait MongoEnvironment {

  val driver: MongoDriver

  val db: DB

}