package edu.url.lasalle.wotgraph.infrastructure.repository.mongodb

import reactivemongo.api.{DB, MongoDriver}

trait MongoEnvironment {

  val driver: MongoDriver

  val db: DB

}