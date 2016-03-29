package edu.url.lasalle.wotgraph.infrastructure.repository.neo4j

import java.net.URI

object Neo4jConf {

  case class Config(
                   credentials: Credentials,
                   server: URI,
                   packages: Seq[String]
                 )

  case class Credentials(
                          username: String,
                          password: String
                        )

}
