package edu.url.lasalle.wotgraph.infrastructure.repository.neo4j

import play.api.libs.ws.{WSAuthScheme, WSClient, WSRequest}

import scala.concurrent.ExecutionContext

case class Neo4jConfig(wsClient: WSClient, domain: String, httpProtocol: String, user: String, password: String)


object Neo4jWebServiceRepository {

  def createPreparedRequest(config: Neo4jConfig): WSRequest = {
    config.wsClient
      .url(s"${config.httpProtocol}://${config.domain}/db/data/transaction/commit")
      .withHeaders(
        "Accept" -> "application/json; charset=UTF-8"
        , "Content-Type" -> "application/json")
      .withAuth(config.user, config.password, WSAuthScheme.BASIC)
  }

}

