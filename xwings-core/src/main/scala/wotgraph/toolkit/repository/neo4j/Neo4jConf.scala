package wotgraph.toolkit.repository.neo4j

import java.net.URI

import org.neo4j.ogm.config.Configuration

object Neo4jConf {

  case class Config(
                   sessionConfig: Configuration,
                   packages: List[String]
                 )

}
